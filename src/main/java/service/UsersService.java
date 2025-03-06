package service;

import dto.UsersDTO;
import mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsersService {
    @Autowired
    UsersMapper usersMapper;

    public void signUp(String name, String profileImage, String email) {
        usersMapper.signUp(name, profileImage, email);
    }

    public int chkMemberKakao(String name, String profileImage) {
        return usersMapper.chkMemberKakao(name, profileImage);
    }

    public int chkMember(String email) {
        return usersMapper.chkMember(email);
    }

    public UsersDTO getUserByEmail(String email) {
        return usersMapper.getUserByEmail(email);
    }
    public UsersDTO getUserById(int userId) {
        return usersMapper.getUserById(userId);
    }
}
