package service;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mapper.GroupsMapper;

@Service
@AllArgsConstructor
public class GroupsService {
	GroupsMapper groupsMapper;

}
