package dto;

import lombok.Data;

@Data
public class SearchDTO {
	private String keyword = "";
	private int page = 1;
	private int size = 10;
	private String orderBy = "created_at";
	private String sort = "DESC";
}
