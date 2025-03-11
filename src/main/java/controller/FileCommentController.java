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

import dto.FileCommentDTO;
import dto.FilesDTO;
import dto.GroupsDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import service.FileCommentService;
import service.FilesService;
import service.GroupsService;

@RestController
@RequestMapping("/api/group/{hashedId}/file/{fileId}/comment")
@RequiredArgsConstructor
public class FileCommentController {
	final FileCommentService fileCommentService;
	final FilesService filesService;
	final GroupsService groupsService;

	@GetMapping
	public ResponseEntity<Object> get(@PathVariable String hashedId, @PathVariable int fileId) {
		Map<String, Object> response = new HashMap<String, Object>();

		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "파일 목록 조회 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		FilesDTO file = filesService.readFileById(fileId);
		if (file == null) {
			response.put("status", "fail");
			response.put("message", "파일 목록 조회 실패: 파일을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		try {
			List<FileCommentDTO> comments = fileCommentService.readFlieCommentListByFileId(fileId);
			response.put("comments", comments);
			response.put("status", "success");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.put("status", "fail");
			response.put("message", "댓글 목록 조회 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping
	public ResponseEntity<Object> post(@PathVariable String hashedId, @PathVariable int fileId, @RequestParam(required = false) Integer commentId,
			@RequestParam String comment, HttpSession session) {
		int userId = (int) session.getAttribute("userId");
		Map<String, Object> response = new HashMap<String, Object>();
		GroupsDTO group = groupsService.readGroupByHashedId(hashedId);
		if (group == null) {
			response.put("status", "fail");
			response.put("message", "댓글 작성 실패: 그룹을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}

		FilesDTO file = filesService.readFileById(fileId);
		if (file == null) {
			response.put("status", "fail");
			response.put("message", "댓글 작성 실패: 파일을 찾을 수 없음");
			return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
		}
		FileCommentDTO parentComment = null;
		if (commentId != null) {
			parentComment = fileCommentService.readFileCommentById(commentId);
			if (commentId != 0 && parentComment == null) {
				response.put("status", "fail");
				response.put("message", "댓글 작성 실패: 첫 댓글을 찾을 수 없음");
				return new ResponseEntity<Object>(response, HttpStatus.NOT_FOUND);
			}
		}

		try {
			FileCommentDTO fileComment = new FileCommentDTO();
			fileComment.setUserId(userId);
			fileComment.setFileId(fileId);
			if (parentComment != null) {
				fileComment.setCommentId(parentComment.getId());
			}
			fileComment.setComment(comment);
			fileCommentService.createFileComment(fileComment);
			fileCommentService.updateFileComment(fileComment);
			response.put("status", "success");
			response.put("message", "댓글 작성 성공");
			return new ResponseEntity<Object>(response, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "fail");
			response.put("message", "댓글 작성 실패");
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
