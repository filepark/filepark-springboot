package service;

import mapper.UsersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UsersService {
    @Autowired
    UsersMapper usersMapper;
    public void signUp(String name, String profileImage) {
        usersMapper.signUp(name, profileImage);
    }
    public int chkMember(String name, String profileImage) {
        return usersMapper.chkMember(name, profileImage);
    }
}
