package mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import dto.FilesDTO;

@Mapper
public interface FilesMapper {
	public void createFile(FilesDTO fileDTO);

	public List<FilesDTO> readFileListByGroupId(int groupId);

	public int readFileCountByGroupId(int groupId);

	public List<FilesDTO> readFileListByUserId(int userId);

	public int readFileCountByUserId(int userId);

	public FilesDTO readFileById(int id);
}
