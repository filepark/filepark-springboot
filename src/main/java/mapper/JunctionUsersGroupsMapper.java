package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dto.GroupsDTO;

@Mapper
public interface JunctionUsersGroupsMapper {
	public List<GroupsDTO> readGroupListByUserId(int userId);
	public int readGroupCountByUserId(int userId);
}
