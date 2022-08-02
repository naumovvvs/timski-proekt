package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.Chat;
import mk.ukim.finki.timskiproekt.model.Message;
import mk.ukim.finki.timskiproekt.model.dto.SaveMessageDto;

import java.util.List;

public interface ChatService {
    Chat getChat(Long id);
    Chat createChatBySessionId(Long sessionId);
    Message getPinnedMessageByChat(Long chatId);
    void clearChat(Long chatId);
    Message pinMessageById(Long messageId, Long chatId);
    void clearPinnedMessage(Long chatId);
    Message saveMessageToChatByRoom(Long roomId, SaveMessageDto messageDto);
    List<Message> getAllMessagesFromChat(Long chatId);
}
