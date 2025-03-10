package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dto.DirectoryDTO;

@Mapper
public interface DirectoryMapper {
	public void createDirectory(DirectoryDTO directoryDTO);
	
	public DirectoryDTO readDirectoryById(int id);
	
	public DirectoryDTO readRootDirectoryByGroupId(int groupId);

	public DirectoryDTO readDirectoryByGroupIdAndDirectoryIdAndDirectoryName(int groupId, int directoryId, String directoryName);
	
	public List<DirectoryDTO> readDirectoryListByGroupIdAndDirectoryId(int groupId, int directoryId);
	
	public void updateDirectoryById(DirectoryDTO directoryDTO);
}
