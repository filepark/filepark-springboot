package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatDto {
//    public enum MessageType{
//        JOIN, TALK, LEAVE
//    }

//    private MessageType messageType; // 메시지 타입
    private String message; // 메시지
    private int sender;
    private int groupId;
}