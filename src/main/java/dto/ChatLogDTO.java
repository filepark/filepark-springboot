package dto;

import java.sql.Timestamp;

import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Alias("ChatLogDTO")
@Data
public class ChatLogDTO {
	private int id;
	private int userId;
	private int groupId;
	private String message;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
	private Timestamp created_at;
	private String userName;
	private String profileImage;
}
