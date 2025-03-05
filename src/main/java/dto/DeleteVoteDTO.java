package dto;

import org.apache.ibatis.type.Alias;

import lombok.Data;

@Alias("DeleteVoteDTO")
@Data
public class DeleteVoteDTO {
	private int user_id;
	private int file_id;
}
