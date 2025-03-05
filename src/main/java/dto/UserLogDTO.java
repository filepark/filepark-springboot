package dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("UserLogDTO")
@Data
public class UserLogDTO {
	private long id;
	private int user_id;
	private String subject;
	private String action;
}
