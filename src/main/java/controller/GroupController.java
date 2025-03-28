package controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dto.DirectoryDTO;
import dto.GroupsDTO;
import dto.JunctionUsersGroupsDTO;
import dto.UsersDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import service.*;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {
	final GroupsService groupsService;
	final UsersService usersService;
	final JunctionUsersGroupsService junctionUsersGroupsService;
	final DirectoryService directoryService;
	private final ObjectStorageService objectStorageService;

	@GetMapping("/invite")
	public ResponseEntity<Object> getInvite(@RequestParam(name = "code") String hashedId, HttpSession session) {
		int userId = (int) session.getAttribute("userId");
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "그룹이 존재하지 않습니다.");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
		
		JunctionUsersGroupsDTO junction = junctionUsersGroupsService.readJunctionUsersGroupsByUserIdAndGroupId(userId, group.getId());
		if (junction != null) {
			response.put("status", "fail");
			response.put("message", "이미 초대된 사용자입니다.");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			junction = new JunctionUsersGroupsDTO();
			junction.setGroupId(group.getId());
			junction.setUserId(userId);
			junctionUsersGroupsService.createJunctionUsersGroups(junction);
			response.put("status", "success");
			response.put("message", "초대 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "초대 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping
	public ResponseEntity<Object> get(HttpSession session) {
		Map<String, Object> response = new HashMap<String, Object>();
		session.getAttribute("userId");
		int userId = (int) session.getAttribute("userId");

		try {
			List<GroupsDTO> groups = junctionUsersGroupsService.readGroupListByUserId(userId);
			int totalGroupCount = junctionUsersGroupsService.readGroupCountByUserId(userId);
			response.put("totalCount", totalGroupCount);
			response.put("groups", groups);
			response.put("status", "success");
			response.put("message", "그룹 목록 조회 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "그룹 목록 조회 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{hashedId}/user")
	public ResponseEntity<Object> getUser(@PathVariable String hashedId) {
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "그룹이 존재하지 않습니다.");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
			List<UsersDTO> users = junctionUsersGroupsService.readUserListByGroupId(group.getId());
			int totalUserCount = junctionUsersGroupsService.readUserCountByGroupId(group.getId());
			response.put("totalCount", totalUserCount);
			response.put("users", users);
			response.put("status", "success");
			response.put("message", "그룹 사용자 목록 조회 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);			
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "그룹 사용자 목록 조회 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@PostMapping
	public ResponseEntity<Object> post(@RequestParam String groupName, @RequestParam String description,
			@RequestParam int maxUser, HttpSession session) {
		Map<String, Object> response = new HashMap<String, Object>();
		int userId = (int) session.getAttribute("userId");
		try {
			GroupsDTO group = new GroupsDTO();
			group.setUserId(userId);
			group.setName(groupName);
			group.setDescription(description);
			group.setMaxUser(maxUser);
			groupsService.createGroup(group);
			
			JunctionUsersGroupsDTO junction = new JunctionUsersGroupsDTO();
			junction.setGroupId(group.getId());
			junction.setUserId(userId);
			junctionUsersGroupsService.createJunctionUsersGroups(junction);
			
			System.out.println("---");
			DirectoryDTO directory = new DirectoryDTO();
			directory.setUserId(userId);
			directory.setGroupId(group.getId());
			directory.setDirectoryName("");
			directory.setIsRoot(1);
			directoryService.createDirectory(directory);
			System.out.println(directory);
			directory.setDirectoryId(directory.getId());
			directoryService.updateDirectoryById(directory);
			
			response.put("status", "success");
			response.put("groupId", group.getHashedId());
			response.put("message", "그룹 생성 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "그룹 생성 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{groupId}/readGroup")
	public GroupsDTO readGroupById(@PathVariable int groupId) {
		GroupsDTO groupsDTO = groupsService.readGroupById(groupId);
		groupsDTO.setHost(usersService.getUserById(groupsDTO.getUserId()));
		return groupsDTO;
	}

	@PostMapping("/{groupId}/update")
	public ResponseEntity<Object> updateGroup(@PathVariable int groupId,
											  @RequestParam String description,
											  @RequestParam String name,
											  @RequestParam int maxUser,
											  @RequestParam(value = "upload", required = false) MultipartFile upload) {
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			GroupsDTO groupsDTO = new GroupsDTO();
			groupsDTO.setId(groupId);
			groupsDTO.setDescription(description);
			groupsDTO.setName(name);
			groupsDTO.setMaxUser(maxUser);
			if (upload != null) {
				String filename = objectStorageService.uploadFile(objectStorageService.getBucketName(),"profile",upload);
				groupsDTO.setGroupImage(objectStorageService.getEndPoint() + "/" + objectStorageService.getBucketName() + "/profile/" + filename);
			}
			groupsService.updateGroupById(groupsDTO);
			response.put("status", "success");
			response.put("groupId", groupsDTO.getId());
			response.put("message", "그룹 변경 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "그룹 변경 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("{groupId}/deleteUser")
	public ResponseEntity<Object> deleteUser(@PathVariable int groupId, @RequestParam String userIds) {
		Map<String, Object> response = new HashMap<String, Object>();
		List<Integer> userIdList = Arrays.stream(userIds.split(","))
				.map(Integer::parseInt)
				.collect(Collectors.toList());
		// 사용자 삭제 처리
		junctionUsersGroupsService.deleteUsersFromGroup(groupId, userIdList);

		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
}
