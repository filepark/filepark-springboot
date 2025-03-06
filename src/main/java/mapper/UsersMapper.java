package mapper;

import dto.UsersDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper {
    public void signUp (String name, String profileImage,String email);
    public int chkMemberKakao (String name, String profileImage);
    public int chkMember (String email);
    public UsersDTO getUserByEmail (String email);
    public UsersDTO getUserById (int userId);
}
