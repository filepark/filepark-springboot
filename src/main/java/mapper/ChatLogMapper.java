package mapper;

import dto.ChatLogDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatLogMapper {
    public void createChatLog(ChatLogDTO chatLogDTO);
    public List<ChatLogDTO> readChatLogByGroupId(int groupId);
}
