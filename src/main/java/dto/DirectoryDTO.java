package dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("DirectoryDTO")
@Data
public class DirectoryDTO {
	private int id;
	private int userId;
	private int groupId;
	private int directoryId;
	private String directoryName;
	private boolean isRoot;
	private String createdAt;
	private String updatedAt;
}
