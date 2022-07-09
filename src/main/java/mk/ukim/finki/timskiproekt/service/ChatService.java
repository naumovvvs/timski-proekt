package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.Chat;
import mk.ukim.finki.timskiproekt.model.Message;
import mk.ukim.finki.timskiproekt.model.Session;

import java.util.List;

public interface ChatService {
    Chat createChat(Session chatSession);
    Message getPinnedMessageByChat(Long chatId);
    void clearChat(Long chatId);
    Message pinMessage(Message message, Long chatId);
    void clearPinnedMessage(Long chatId);
    Message addMessageToChat(Long chatId, Message message);
    List<Message> getAllMessagesFromChat(Long chatId);
}
