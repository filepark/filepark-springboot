package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dto.DirectoryDTO;
import dto.FilesDTO;
import dto.GroupsDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import service.DirectoryService;
import service.FilesService;
import service.GroupsService;
import service.ObjectStorageService;

@RestController
@RequestMapping("/api/group/{hashedId}/directory")
@RequiredArgsConstructor
public class DirectoryController {
	final DirectoryService directoryService;
	final FilesService filesService;
	final GroupsService groupsService;
	final ObjectStorageService objectStorageService;

	@PostMapping
	public ResponseEntity<Object> post(@PathVariable String hashedId, @RequestParam String directoryName, @RequestParam String path,
			HttpSession session) {
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

		DirectoryDTO existingDirectory = directoryService.readDirectoryByGroupIdAndDirectoryIdAndDirectoryName(group.getId(), directory.getId(),
				directoryName);
		System.out.println("existingDirectory: " + existingDirectory);
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
			newDirectory.setIsRoot(0);
			System.out.println("newDirectory: " + newDirectory);
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

	@PutMapping("/{directoryId}")
	public ResponseEntity<Object> put(@PathVariable String hashedId, @PathVariable int directoryId,
			@RequestParam(name = "fileName") String directoryName) {
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "폴더 이름 변경 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		DirectoryDTO directory = directoryService.readDirectoryById(directoryId);
		if (directory == null) {
			response.put("status", "fail");
			response.put("message", "폴더 이름 변경 실패: 유효하지 않은 폴더");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		DirectoryDTO existingDirectory = directoryService.readDirectoryByGroupIdAndDirectoryIdAndDirectoryName(group.getId(), directory.getId(),
				directoryName);
		if (existingDirectory != null) {
			response.put("status", "fail");
			response.put("message", "폴더 이름 변경 실패: 이미 존재하는 폴더명입니다.");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			directory.setDirectoryName(directoryName);
			directoryService.updateDirectoryById(directory);
			response.put("status", "success");
			response.put("message", "폴더 이름 변경 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "폴더 이름 변경 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Transactional
	@DeleteMapping("/{directoryId}")
	public ResponseEntity<Object> delete(@PathVariable String hashedId, @PathVariable int directoryId) {
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "폴더 삭제 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		DirectoryDTO directory = directoryService.readDirectoryById(directoryId); // 삭제할 디렉토리 조회
		if (directory == null) {
			response.put("status", "fail");
			response.put("message", "폴더 삭제 실패: 유효하지 않은 폴더");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		List<FilesDTO> deleteFileList = new ArrayList<>();
		Queue<Integer> visitDirectory = new LinkedList<>();
		List<DirectoryDTO> subDirectories = directoryService.readDirectoryListByGroupIdAndDirectoryId(group.getId(), directoryId);
		deleteFileList.addAll(filesService.readFileListByGroupIdAndDirectoryId(group.getId(), directoryId));
		for (DirectoryDTO subDirectory : subDirectories) {
			visitDirectory.add(subDirectory.getId());
		}

		while (visitDirectory.size() > 0) {
			int subDirectoryId = visitDirectory.poll();
			subDirectories = directoryService.readDirectoryListByGroupIdAndDirectoryId(group.getId(), subDirectoryId);
			deleteFileList.addAll(filesService.readFileListByGroupIdAndDirectoryId(group.getId(), subDirectoryId));
			for (DirectoryDTO subDirectory : subDirectories) {
				visitDirectory.add(subDirectory.getId());
			}
		}
		try {
			for (FilesDTO file : deleteFileList) {
				objectStorageService.deleteFile(objectStorageService.getBucketName(), "files", file.getStoredName());
			}
			directoryService.deleteDirectoryById(directoryId); // cascade delete
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
