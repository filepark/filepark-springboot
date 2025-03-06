package mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UsersMapper {
    public void signUp (String name, String profileImage);
    public int chkMember (String name, String profileImage);
}
