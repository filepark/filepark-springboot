package mapper;

import org.apache.ibatis.annotations.Mapper;

import dto.GroupsDTO;

@Mapper
public interface GroupsMapper {
	public void createGroup(GroupsDTO group);

	public GroupsDTO readGroupById(int id);
}
