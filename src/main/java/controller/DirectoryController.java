package controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dto.DirectoryDTO;
import dto.GroupsDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import service.DirectoryService;
import service.GroupsService;

@RestController
@RequestMapping("/api/group/{hashedId}/directory")
@RequiredArgsConstructor
public class DirectoryController {
	final DirectoryService directoryService;
	final GroupsService groupsService;

	@PostMapping
	public ResponseEntity<Object> post(@PathVariable String hashedId, @RequestParam String directoryName, @RequestParam String path,
			HttpServletRequest request, HttpSession session) {
		int userId = (int) session.getAttribute("userId");
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "새 폴더 생성 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		DirectoryDTO directory = directoryService.readDirectoryByDirectoryPath(group.getId(), path);
		if (directory == null) {
			response.put("status", "fail");
			response.put("message", "새 폴더 생성 실패: 유효하지 않은 경로");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		DirectoryDTO existingDirectory = directoryService.readDirectoryByGroupIdAndDirectoryIdAndDirectoryName(group.getId(), directory.getId(), directoryName);
		if (existingDirectory != null) {
			response.put("status", "fail");
			response.put("message", "새 폴더 생성 실패: 이미 존재하는 폴더명입니다.");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			DirectoryDTO newDirectory = new DirectoryDTO();
			newDirectory.setUserId(userId);
			newDirectory.setGroupId(group.getId());
			newDirectory.setDirectoryId(directory.getId());
			newDirectory.setDirectoryName(directoryName);
			directoryService.createDirectory(newDirectory);
			response.put("status", "success");
			response.put("message", "새 폴더 생성 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "새 폴더 생성 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Transactional
	@DeleteMapping("/{directoryId}")
	public ResponseEntity<Object> delete(@PathVariable String hashedId, @PathVariable int directoryId, HttpServletRequest request,
			HttpSession session) {
		int userId = (int) session.getAttribute("userId");
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "폴더 삭제 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		DirectoryDTO directory = directoryService.readDirectoryById(directoryId);
		// 1. 디렉토리 조회
		// 2. 디렉토리가 존재하면 -> 1
		// 3. 디렉토리가 존재하지 않으면 해당 디렉토리를 참조하는 파일 삭제, 해당 디렉토리 삭제, return
		while (directory != null) {
			// 디렉토리에 속한 파일 삭제
			directoryService.deleteFileByDirectoryId(directory.getId());
			// 디렉토리 삭제
			directoryService.deleteDirectoryById(directory.getId());
			// 상위 디렉토리로 이동
			directory = directoryService.readDirectoryById(directory.getDirectoryId());
		}
		
		
		if (directory == null) {
			response.put("status", "fail");
			response.put("message", "폴더 삭제 실패: 유효하지 않은 폴더");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			directoryService.deleteDirectoryById(directoryId);
			response.put("status", "success");
			response.put("message", "폴더 삭제 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "폴더 삭제 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
