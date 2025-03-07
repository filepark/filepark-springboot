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
import org.springframework.web.multipart.MultipartFile;

import dto.FilesDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import service.FilesService;
import service.GroupsService;
import service.JunctionUsersGroupsService;
import service.ObjectStorageService;
import service.UsersService;

@RestController
@RequestMapping("/api/group/{groupId}/file")
@RequiredArgsConstructor
public class FileController {
	final GroupsService groupsService;
	final UsersService usersService;
	final JunctionUsersGroupsService junctionUsersGroupsService;
	final FilesService filesService;
	final ObjectStorageService objectStorageService;

	private static final String BUCKET_NAME = "bucket-name";

	@GetMapping("/list")
	public ResponseEntity<Object> fileList(@PathVariable int groupId, HttpSession session) {
		Map<String, Object> response = new HashMap<String, Object>();
		List<FilesDTO> files = null;
		try {
			files = filesService.readFileListByGroupId(groupId);
			int fileCount = filesService.readFileCountByGroupId(groupId);
			response.put("totalCount", fileCount);
			response.put("files", files);
			response.put("status", "success");
			response.put("message", "파일 목록 조회 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "파일 목록을 가져오는 데 실패했습니다.");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/upload")
	public ResponseEntity<Object> uploadFile(@PathVariable int groupId, @RequestParam String path,
			@RequestParam MultipartFile file, HttpSession session) {
		int userId = (int) session.getAttribute("userId");
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String storedName = objectStorageService.uploadFile(BUCKET_NAME, "files", file);
			FilesDTO fileDTO = new FilesDTO();
			fileDTO.setGroupId(groupId);
			fileDTO.setUserId(userId);
			fileDTO.setFileName(file.getOriginalFilename());
			fileDTO.setStoredName(storedName);
			fileDTO.setFilePath(path);
			filesService.createFile(fileDTO);
			response.put("status", "success");
			response.put("message", "파일 업로드 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "파일 업로드 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
