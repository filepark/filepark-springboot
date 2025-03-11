package dto;

import java.sql.Timestamp;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Alias("GroupsDTO")
@Data
public class GroupsDTO {
	private int id;
	private String hashedId;
	private int userId;
	private UsersDTO host;
	private String name;
	private String description;
	private int maxUser;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private Timestamp createdAt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private Timestamp updatedAt;
}
