package websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ChatLogDTO;
import dto.GroupsDTO;
import dto.UsersDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import service.ChatLogService;
import service.GroupsService;
import service.JunctionUsersGroupsService;
import service.UsersService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketChatHandler extends TextWebSocketHandler {
	@Autowired
	UsersService usersService;
	@Autowired
	ChatLogService chatLogService;
	@Autowired
	JunctionUsersGroupsService junctionUsersGroupsService;
	@Autowired
	GroupsService groupsService;

	private static final Map<Integer, Set<WebSocketSession>> GROUP_SESSIONS = new ConcurrentHashMap<>();
	private static final Map<WebSocketSession, Integer> SESSION_GROUPS = new ConcurrentHashMap<>();

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//		System.out.println("ID: " + session.getAttributes().get("userId"));
		Integer userId = (Integer) session.getAttributes().get("userId");
		if (userId == null) {
			return;
		}
		List<Integer> userGroups = getUserGroups(userId);
		for (int groupId : userGroups) {
			SESSION_GROUPS.put(session, groupId); // ✅ 세션을 그룹 ID와 매핑

		}
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        Integer userId = (Integer) session.getAttributes().get("userId");
//
//        if (userId != null) {
//            List<Integer> userGroups = getUserGroups(userId);
//
//            for (int groupId : userGroups) {
//                Map<Integer, WebSocketSession> userGroupSessions = CLIENTS.get(String.valueOf(userId));
//                if (userGroupSessions != null) {
//                    userGroupSessions.remove(groupId); // 해당 그룹에서 세션 제거
//                    if (userGroupSessions.isEmpty()) {
//                        CLIENTS.remove(String.valueOf(userId)); // 유저가 더 이상 속한 그룹이 없으면 유저도 제거
//                    }
//                }
//            }
//        }
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		String payload = message.getPayload();
		if (payload.startsWith("new::")) {
			String[] parts = payload.split("::");
			int userId = Integer.parseInt(parts[1]); // 사용자 ID
			GroupsDTO group = groupsService.readGroupByHashedId(parts[2]);
			int groupId = group.getId(); // 그룹 ID

			// 해당 그룹 ID에 대한 세션 목록에 현재 세션 추가
			GROUP_SESSIONS.computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet()).add(session);

			// 세션에 사용자 정보 저장 (전역 변수 사용 방지)
			session.getAttributes().put("userId", userId);
			session.getAttributes().put("groupId", groupId);
			return;
		}

		// JSON 데이터를 파싱하여 payloadData 맵으로 변환
		Map<String, String> payloadData = new HashMap<>();
		payload = payload.replace("{", "").replace("}", "").replace("\"", ""); // JSON 기호 제거
		String[] keyValuePairs = payload.split(",");

		for (String pair : keyValuePairs) {
			String[] entry = pair.split(":");
			if (entry.length == 2) {
				payloadData.put(entry[0].trim(), entry[1].trim());
			}
		}

		// 세션에서 userId 및 groupId 가져오기
		Integer sendUserId = (Integer) session.getAttributes().get("userId");
		Integer sendGroupId = (Integer) session.getAttributes().get("groupId");

		if (sendUserId == null || sendGroupId == null) {
			return;
		}

		// 유저 정보 가져오기
		UsersDTO usersDTO = usersService.getUserById(sendUserId);
		String originalImage = usersDTO.getProfileImage();
		String imgUrl = originalImage.startsWith("http") ? originalImage
				: "https://kr.object.ncloudstorage.com/filepark-bucket/profile/" + originalImage;

		// 현재 시간 포맷팅
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String formattedTime = sdf.format(new Date());

		// 채팅 데이터 생성
		Map<String, Object> chatData = new HashMap<>();
		chatData.put("id", sendUserId);
		chatData.put("name", usersDTO.getName());
		chatData.put("profileImage", imgUrl);
		chatData.put("message", payloadData.get("message"));
		chatData.put("groupId", sendGroupId);
		chatData.put("sendTime", formattedTime);

		String chatMessage = new ObjectMapper().writeValueAsString(chatData);

		// 채팅 로그 DB 저장
		ChatLogDTO chatLogDTO = new ChatLogDTO();
		chatLogDTO.setUserId(sendUserId);
		chatLogDTO.setMessage(payloadData.get("message"));
		chatLogDTO.setGroupId(sendGroupId);

		// 채팅 데이터 검증
		String messageText = payloadData.get("message");
		if (messageText == null) {
			return;
		}

		// 해당 그룹의 WebSocket 세션 가져오기
		Set<WebSocketSession> groupSessions = GROUP_SESSIONS.getOrDefault(sendGroupId, new HashSet<>());
		if (!groupSessions.isEmpty()) {
			chatLogService.createChatLog(chatLogDTO);
		}

		for (WebSocketSession groupSession : groupSessions) {
			if (groupSession.isOpen()) {
				try {
					groupSession.sendMessage(new TextMessage(chatMessage));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private List<Integer> getUserGroups(int userId) {
		return junctionUsersGroupsService.readGroupsByUserId(userId);
	}
}
