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
	private UsersDTO owner;
	private int groupId;
	private GroupsDTO group;
	private int directoryId;
	private String fileName;
	private String storedName;
	private String fileUri;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private Timestamp createdAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private Timestamp updatedAt;
}
