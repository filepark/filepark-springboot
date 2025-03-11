package service;

import dto.ChatLogDTO;
import mapper.ChatLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.List;

@Service
@AllArgsConstructor
public class ChatLogService {
    @Autowired
    ChatLogMapper chatLogMapper;

    public void createChatLog(ChatLogDTO chatLogDTO) {
        chatLogMapper.createChatLog(chatLogDTO);
    };
    public List<ChatLogDTO> getChatLog(int groupId) {
        return chatLogMapper.readChatLogByGroupId(groupId);
    };
}
