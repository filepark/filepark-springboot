package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import service.DirectoryService;
import service.GroupsService;
import service.JunctionUsersGroupsService;
import service.UsersService;

@RestController
@RequestMapping("/api/group")
@RequiredArgsConstructor
public class GroupController {
	final GroupsService groupsService;
	final UsersService usersService;
	final JunctionUsersGroupsService junctionUsersGroupsService;
	final DirectoryService directoryService;

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
			
			DirectoryDTO directory = new DirectoryDTO();
			directory.setUserId(userId);
			directory.setGroupId(group.getId());
			directoryService.createDirectory(directory);
			directoryService.updateDirectoryById(directory);
			
			response.put("status", "success");
			response.put("groupId", group.getId());
			response.put("message", "그룹 생성 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
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
}
