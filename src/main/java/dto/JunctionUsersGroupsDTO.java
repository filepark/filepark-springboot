package dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("JunctionUsersGroupsDTO")
@Data
public class JunctionUsersGroupsDTO {
	private int user_id;
	private int group_id;
}
