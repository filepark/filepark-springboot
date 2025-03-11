package service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import dto.GroupsDTO;
import lombok.AllArgsConstructor;
import mapper.GroupsMapper;

@Service
@AllArgsConstructor
public class GroupsService {
	GroupsMapper groupsMapper;
	
	public void createGroup(GroupsDTO group) {
		String value = String.valueOf(System.currentTimeMillis());
		value += group.getName();
		
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedHash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
			
			StringBuilder hexString = new StringBuilder();
			for (byte b : encodedHash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			group.setHashedId(hexString.toString());
			
			groupsMapper.createGroup(group);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public GroupsDTO readGroupById(int id) {
		return groupsMapper.readGroupById(id);
	}
	
	public GroupsDTO readGroupByHashedId(String hashedId) {
		return groupsMapper.readGroupByHashedId(hashedId);
	}

	public void updateGroupById(GroupsDTO group) {
		groupsMapper.updateGroupById(group);
	}
}
