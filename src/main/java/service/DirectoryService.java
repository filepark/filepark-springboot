package service;

import java.util.List;

import org.springframework.stereotype.Service;

import dto.DirectoryDTO;
import lombok.AllArgsConstructor;
import mapper.DirectoryMapper;

@Service
@AllArgsConstructor
public class DirectoryService {
	DirectoryMapper directoryMapper;

	public void createDirectory(DirectoryDTO directoryDTO) {
		directoryMapper.createDirectory(directoryDTO);
	}

	public DirectoryDTO readDirectoryById(int id) {
		return directoryMapper.readDirectoryById(id);
	}
	
	public DirectoryDTO readRootDirectoryByGroupId(int groupId) {
		return directoryMapper.readRootDirectoryByGroupId(groupId);
	}

	public DirectoryDTO readDirectoryByGroupIdAndDirectoryIdAndDirectoryName(int groupId, int directoryId, String directoryName) {
		return directoryMapper.readDirectoryByGroupIdAndDirectoryIdAndDirectoryName(groupId, directoryId, directoryName);
	}

	public List<DirectoryDTO> readDirectoryListByGroupIdAndDirectoryId(int groupId, int directoryId) {
		return directoryMapper.readDirectoryListByGroupIdAndDirectoryId(groupId, directoryId);
	}
	
	public DirectoryDTO readDirectoryByDirectoryPath(int groupId, String path) {
		String[] pathParts = path.split("/");
		DirectoryDTO directory = readRootDirectoryByGroupId(groupId);
		if (directory == null) {
			return null;
		}
		for (String part : pathParts) {
			directory = readDirectoryByGroupIdAndDirectoryIdAndDirectoryName(groupId, directory.getId(), part);
			if (directory == null) {
				return null;
			}
		}
		return directory;
	}

	public void updateDirectoryById(DirectoryDTO directoryDTO) {
		directoryMapper.updateDirectoryById(directoryDTO);
	}
}
