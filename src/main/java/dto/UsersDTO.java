package dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("UserDTO")
@Data
public class UsersDTO {
	private int id;
	private String email;
	private String name;
	private String phone;
	private String birth;
	private String profile_image;
	private String description;
	private String provider;
	private int is_admin;
	private String created_at;
	private String updated_at;
	private String deleted_at;
}
