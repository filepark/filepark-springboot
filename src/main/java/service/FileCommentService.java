package service;

import java.util.List;

import org.springframework.stereotype.Service;

import dto.FileCommentDTO;
import dto.UsersDTO;
import lombok.AllArgsConstructor;
import mapper.FileCommentMapper;
import mapper.UsersMapper;

@Service
@AllArgsConstructor
public class FileCommentService {
	FileCommentMapper fileCommentMapper;
	UsersMapper usersMapper;

	public void createFileComment(FileCommentDTO fileComment) {
		System.out.println(fileComment);
		fileCommentMapper.createFileComment(fileComment);
	}

	public FileCommentDTO readFileCommentById(int id) {
		return fileCommentMapper.readFileCommentById(id);
	}

	public List<FileCommentDTO> readFlieCommentListByFileId(int fileId) {
		List<FileCommentDTO> list = fileCommentMapper.readFlieCommentListByFileId(fileId);
		list.forEach(comment -> {
			UsersDTO writer = usersMapper.readUserById(comment.getUserId());
			comment.setWriter(writer);
			if (comment.getReplyComment() != null) {
				comment.setReplyComment(readFileCommentById(comment.getReplyComment().getId()));
			}
		});
		return list;
	}

	public int fileCommentCountByFileId(int fileId) {
		return fileCommentMapper.fileCommentCountByFileId(fileId);
	}

	public List<FileCommentDTO> readFlieCommentListByCommentId(int commentId) {
		return fileCommentMapper.readFlieCommentListByCommentId(commentId);
	}

	public void updateFileComment(FileCommentDTO fileComment) {
		fileCommentMapper.updateFileComment(fileComment);
	}

	public void deleteFileCommentById(int id) {
		fileCommentMapper.deleteFileCommentById(id);
	}

	public void absoluteDeleteFileCommentById(int id) {
		fileCommentMapper.absoluteDeleteFileCommentById(id);
	}
}
