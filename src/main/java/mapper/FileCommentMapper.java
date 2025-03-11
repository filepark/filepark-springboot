package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dto.FileCommentDTO;

@Mapper
public interface FileCommentMapper {
	public void createFileComment(FileCommentDTO fileComment);

	public FileCommentDTO readFileCommentById(int id);
	
	public List<FileCommentDTO> readFlieCommentListByFileId(int fileId);
	
	public int fileCommentCountByFileId(int fileId);
	
	public List<FileCommentDTO> readFlieCommentListByCommentId(int commentId);
	
	public void updateFileComment(FileCommentDTO fileComment);
	
	public void deleteFileCommentById(int id);
	
	public void absoluteDeleteFileCommentById(int id);
}
