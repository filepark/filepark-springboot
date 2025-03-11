package dto;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Alias("FileCommentDTO")
@Data
public class FileCommentDTO {
	private int id;
	private Integer commentId;
	private FileCommentDTO replyComment;
	private int userId;
	private UsersDTO writer;
	private int fileId;
	private String comment;
	private String deletedBy;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private String createdAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private String updatedAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private String deletedAt;
}
