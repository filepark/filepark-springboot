package dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("JunctionUsersGroupsDTO")
@Data
public class JunctionUsersGroupsDTO {
	private int userId;
	private int groupId;
}
