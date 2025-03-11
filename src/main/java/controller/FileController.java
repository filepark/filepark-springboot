package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
@RequestMapping("/api/group/{hashedId}/file")
@RequiredArgsConstructor
public class FileController {
	final GroupsService groupsService;
	final FilesService filesService;
	final DirectoryService directoryService;
	final ObjectStorageService objectStorageService;

	@GetMapping
	public ResponseEntity<Object> get(@PathVariable String hashedId, @RequestParam String directoryStr) {
		Map<String, Object> response = new HashMap<String, Object>();
		int directoryId = 0;
		try {
			directoryId = Integer.parseInt(directoryStr);
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "파일 목록 조회 실패: 유효하지 않은 경로");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "파일 목록 조회 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		DirectoryDTO directory = directoryService.readDirectoryById(directoryId);
		if (directory == null) {
			response.put("status", "fail");
			response.put("message", "파일 목록 조회 실패: 유효하지 않은 경로");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			List<FilesDTO> files = filesService.readFileListByGroupIdAndDirectoryId(group.getId(), directoryId);
			List<DirectoryDTO> directories = directoryService.readDirectoryListByGroupIdAndDirectoryId(group.getId(), directory.getId());
			int directoryCount = directories.size();
			int fileCount = files.size();
			response.put("totalCount", directoryCount + fileCount);
			response.put("directories", directories);
			response.put("files", files);
			response.put("status", "success");
			response.put("message", "파일 목록 조회 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "파일 목록 조회 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/{fileId}")
	public ResponseEntity<Object> get(@PathVariable String hashedId, @PathVariable int fileId) {
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "파일 조회 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		FilesDTO file = filesService.readFileById(fileId);
		if (file == null) {
			response.put("status", "fail");
			response.put("message", "파일 조회 실패: 파일을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		response.put("file", file);
		response.put("status", "success");
		response.put("message", "파일 조회 성공");
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<Object> post(@PathVariable String hashedId, @RequestParam String fileName, @RequestParam String path,
			@RequestParam MultipartFile file, HttpSession session) {
		int userId = (int) session.getAttribute("userId");
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "파일 업로드 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		DirectoryDTO directory = directoryService.readDirectoryByDirectoryPath(group.getId(), path);
		if (directory == null) {
			response.put("status", "fail");
			response.put("message", "파일 업로드 실패: 유효하지 않는 경로");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		FilesDTO fileDto = filesService.readFileByGroupIdAndFileName(group.getId(), fileName);
		if (fileDto != null) {
			response.put("status", "fail");
			response.put("message", "파일 업로드 실패: 파일명 중복");
			return new ResponseEntity<Object>(response, HttpStatus.BAD_REQUEST);
		}

		String storedName = objectStorageService.uploadFile(objectStorageService.getBucketName(), "files", file);
		try {
			fileDto = new FilesDTO();
			fileDto.setUserId(userId);
			fileDto.setGroupId(group.getId());
			fileDto.setDirectoryId(directory.getId());
			fileDto.setFileName(fileName);
			fileDto.setStoredName(storedName);
			fileDto.setFileUri(objectStorageService.getEndPoint() + "/" + objectStorageService.getBucketName() + "/files/" + storedName);
			filesService.createFile(fileDto);
			response.put("status", "success");
			response.put("message", "파일 업로드 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			objectStorageService.deleteFile(objectStorageService.getBucketName(), "files", storedName);
			response.put("status", "fail");
			response.put("message", "파일 업로드 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/{fileId}")
	public ResponseEntity<Object> put(@PathVariable String hashedId, @PathVariable int fileId, @RequestParam String fileName) {
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "파일 수정 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		FilesDTO fileDto = filesService.readFileById(fileId);
		if (fileDto == null) {
			response.put("status", "fail");
			response.put("message", "파일 수정 실패: 파일을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		try {
			fileDto.setFileName(fileName);
			filesService.updateFile(fileDto);
			response.put("status", "success");
			response.put("message", "파일 수정 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "파일 수정 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/{fileId}")
	public ResponseEntity<Object> delete(@PathVariable String hashedId, @PathVariable int fileId) {
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "파일 삭제 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		FilesDTO file = filesService.readFileById(fileId);
		if (file == null) {
			response.put("status", "fail");
			response.put("message", "파일 삭제 실패: 파일을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		try {
			objectStorageService.deleteFile(objectStorageService.getBucketName(), "files", file.getStoredName());
			filesService.deleteFileById(fileId);
			response.put("status", "success");
			response.put("message", "파일 삭제 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "파일 삭제 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping
	public ResponseEntity<Object> delete(@PathVariable String hashedId, @RequestParam List<Integer> fileIds) {
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "파일 삭제 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		try {
			for (int fileId : fileIds) {
				FilesDTO file = filesService.readFileById(fileId);
				if (file != null) {
					objectStorageService.deleteFile(objectStorageService.getBucketName(), "files", file.getStoredName());
					filesService.deleteFileById(fileId);
				}
			}
			response.put("status", "success");
			response.put("message", "파일 삭제 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "파일 삭제 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
