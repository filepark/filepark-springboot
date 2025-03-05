package mapper;

import org.apache.ibatis.annotations.Mapper;

import dto.UsersDTO;

@Mapper
public interface UsersMapper {
	public UsersDTO readUserById(int id);
}
