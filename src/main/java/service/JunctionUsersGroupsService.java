package service;

import java.util.List;

import org.springframework.stereotype.Service;

import dto.GroupsDTO;
import dto.JunctionUsersGroupsDTO;
import dto.UsersDTO;
import lombok.AllArgsConstructor;
import mapper.JunctionUsersGroupsMapper;
import mapper.UsersMapper;

@Service
@AllArgsConstructor
public class JunctionUsersGroupsService {
	JunctionUsersGroupsMapper junctionUsersGroupsMapper;
	UsersMapper usersMapper;
	public void createJunctionUsersGroups(JunctionUsersGroupsDTO junctionDTO) {
		junctionUsersGroupsMapper.createJunctionUsersGroups(junctionDTO);
	}

	public JunctionUsersGroupsDTO readJunctionUsersGroupsByUserIdAndGroupId(int userId, int groupId) {
		return junctionUsersGroupsMapper.readJunctionUsersGroupsByUserIdAndGroupId(userId, groupId);
	}

	public List<GroupsDTO> readGroupListByUserId(int userId) {
		List<GroupsDTO> list = junctionUsersGroupsMapper.readGroupListByUserId(userId);
		list.forEach(group -> {
			UsersDTO host = usersMapper.readUserById(group.getUserId());
			group.setHost(host);
		});
		return list;
	}

	public int readGroupCountByUserId(int userId) {
		return junctionUsersGroupsMapper.readGroupCountByUserId(userId);
	}

	public List<UsersDTO> readUserListByGroupId(int groupId) {
		return junctionUsersGroupsMapper.readUserListByGroupId(groupId);
	}

	public int readUserCountByGroupId(int groupId) {
		return junctionUsersGroupsMapper.readUserCountByGroupId(groupId);
	}

	public List<Integer> readGroupsByUserId(int userId) {
		return junctionUsersGroupsMapper.readGroupsByUserId(userId);
	}
}
