package service;

import java.util.List;

import org.springframework.stereotype.Service;

import dto.FilesDTO;
import dto.GroupsDTO;
import dto.UsersDTO;
import lombok.AllArgsConstructor;
import mapper.FilesMapper;
import mapper.GroupsMapper;
import mapper.UsersMapper;

@Service
@AllArgsConstructor
public class FilesService {
	FilesMapper filesMapper;
	UsersMapper usersMapper;
	GroupsMapper groupsMapper;

	public void createFile(FilesDTO fileDTO) {
		filesMapper.createFile(fileDTO);
	}

	public List<FilesDTO> readFileListByGroupId(int groupId) {
		List<FilesDTO> list = filesMapper.readFileListByGroupId(groupId);
		list.forEach(file -> {
			UsersDTO owner = usersMapper.readUserById(file.getUserId());
			GroupsDTO group = groupsMapper.readGroupById(file.getGroupId());
			file.setOwner(owner);
			file.setGroup(group);
		});
		return list;
	}

	public int readFileCountByGroupId(int groupId) {
		return filesMapper.readFileCountByGroupId(groupId);
	}

	public List<FilesDTO> readFileListByUserId(int userId) {
		List<FilesDTO> list = filesMapper.readFileListByUserId(userId);
		list.forEach(file -> {
			UsersDTO owner = usersMapper.readUserById(file.getUserId());
			GroupsDTO group = groupsMapper.readGroupById(file.getGroupId());
			file.setOwner(owner);
			file.setGroup(group);
		});
		return list;
	}

	public int readFileCountByUserId(int userId) {
		return filesMapper.readFileCountByUserId(userId);
	}

	public FilesDTO readFileById(int id) {
		FilesDTO file = filesMapper.readFileById(id);
		UsersDTO owner = usersMapper.readUserById(file.getUserId());
		GroupsDTO group = groupsMapper.readGroupById(file.getGroupId());
		file.setOwner(owner);
		file.setGroup(group);
		return file;
	}
}
