package dto;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Alias("FileCommentDTO")
@Data
public class FileCommentDTO {
	private int id;
	private int comment_id;
	private int user_id;
	private int file_id;
	private String comment;
	private String deleted_by;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private String created_at;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private String deleted_at;
}
