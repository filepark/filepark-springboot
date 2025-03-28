package mapper;

import dto.UsersDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper {
	public UsersDTO readUserById(int id);
    
    public void signUp (String name, String profileImage,String email, String provider);

    public int chkMemberKakao (String name, String profileImage);

    public int chkMember (String email);

    public UsersDTO getUserByEmail (String email);

    public UsersDTO getUserById (int userId);

    public void updateUserById(UsersDTO dto);
}
