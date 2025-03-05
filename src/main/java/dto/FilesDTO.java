package dto;

import java.sql.Timestamp;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Alias("FilesDTO")
@Data
public class FilesDTO {
	private int id;
	private int userId;
	private int groupId;
	private String bucketUri;
	private String bucketPath;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private Timestamp createdAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private Timestamp updatedAt;
}
