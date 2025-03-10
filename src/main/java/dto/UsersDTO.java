package dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("UsersDTO")
@Data
public class UsersDTO {
	private int id;
	private String email;
	private String name;
	private String phone;
	private String birth;
	private String profileImage;
	private String description;
	private String provider;
	private int isAdmin;
	private String createdAt;
	private String updatedAt;
	private String deletedAt;
}
