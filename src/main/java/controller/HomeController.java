package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dto.GroupsDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import service.GroupsService;
import service.JunctionUsersGroupsService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {
	final GroupsService groupService;
	final JunctionUsersGroupsService junctionUsersGroupsService;
	
	@GetMapping("/group-list")
	public ResponseEntity<Object> groupList(HttpSession session) {
		Map<String, Object> response = new HashMap<String, Object>();
		List<GroupsDTO> groups = null;
		session.getAttribute("userId");
//		String userId = (String) session.getAttribute("userId");
		String userId = "2";
//		if (userId == null) {
//			response.put("status", "fail");
//			response.put("message", "로그인이 필요합니다.");
//			return new ResponseEntity<Object>(response, HttpStatus.UNAUTHORIZED);
//		}
		
		groups = junctionUsersGroupsService.readGroupListByUserId(Integer.parseInt(userId));
		int toalGroupCount = junctionUsersGroupsService.readGroupCountByUserId(Integer.parseInt(userId));
		response.put("totalGroupCount", toalGroupCount);
		response.put("groups", groups);
		response.put("status", "success");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}
}
