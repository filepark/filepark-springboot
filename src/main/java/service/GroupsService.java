package service;

import org.springframework.stereotype.Service;

import dto.GroupsDTO;
import lombok.AllArgsConstructor;
import mapper.GroupsMapper;

@Service
@AllArgsConstructor
public class GroupsService {
	GroupsMapper groupsMapper;
	
	public void createGroup(GroupsDTO group) {
		groupsMapper.createGroup(group);
	}
	
	public GroupsDTO readGroupById(int id) {
		return groupsMapper.readGroupById(id);
	}
}
