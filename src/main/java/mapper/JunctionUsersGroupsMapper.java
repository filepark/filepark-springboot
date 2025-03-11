package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dto.GroupsDTO;
import dto.JunctionUsersGroupsDTO;
import dto.UsersDTO;

@Mapper
public interface JunctionUsersGroupsMapper {
	public void createJunctionUsersGroups(JunctionUsersGroupsDTO junctionDTO);
	
	public JunctionUsersGroupsDTO readJunctionUsersGroupsByUserIdAndGroupId(int userId, int groupId);

	public List<GroupsDTO> readGroupListByUserId(int userId);

	public int readGroupCountByUserId(int userId);
	
	public List<UsersDTO> readUserListByGroupId(int groupId);
	
	public int readUserCountByGroupId(int groupId);

	public List<Integer> readGroupsByUserId(int userId);
}
