package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.Chat;
import mk.ukim.finki.timskiproekt.model.Message;
import mk.ukim.finki.timskiproekt.model.Session;
import mk.ukim.finki.timskiproekt.model.dto.SaveMessageDto;

import java.util.List;

public interface ChatService {
    Chat getChat(Long id);
    Chat createChat(Session chatSession);
    Chat createChatBySessionId(Long sessionId); // Created for more convenient api use
    Message getPinnedMessageByChat(Long chatId);
    void clearChat(Long chatId);
    Message pinMessage(Message message, Long chatId);
    Message pinMessageById(Long messageId, Long chatId); // Created for more convenient api use
    void clearPinnedMessage(Long chatId);
    Message addMessageToChat(Long chatId, Message message);
    Message saveMessageToChat(Long chatId, SaveMessageDto messageDto); // Created for more convenient api use
    List<Message> getAllMessagesFromChat(Long chatId);
}
