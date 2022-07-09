package mk.ukim.finki.timskiproekt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.Chat;
import mk.ukim.finki.timskiproekt.model.Message;
import mk.ukim.finki.timskiproekt.model.Session;
import mk.ukim.finki.timskiproekt.repository.ChatRepository;
import mk.ukim.finki.timskiproekt.service.ChatService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public Chat createChat(Session chatSession) {
        log.info("Creating chat for session: {}", chatSession.getName());
        return this.chatRepository.save(new Chat(null, null, new ArrayList<>(), chatSession));
    }

    @Override
    public Message getPinnedMessageByChat(Long chatId) {
        log.info("Getting pinned message from chat with id: {}", chatId);

        Optional<Chat> chat = this.chatRepository.findById(chatId);
        if(chat.isPresent()) {
            return chat.get().getPinnedMessage();
        }
        throw new RuntimeException("Chat not found");
    }

    @Override
    public void clearChat(Long chatId) {
        log.info("Trying to clear chat with id: {}", chatId);
        Optional<Chat> optChat = this.chatRepository.findById(chatId);
        if(optChat.isPresent()) {
            Chat chat = optChat.get();
            chat.setMessages(new ArrayList<>());
            chat.setPinnedMessage(null);
            this.chatRepository.save(chat);

            log.info("Cleared chat with id: {}", chatId);
        }
    }

    @Override
    public Message pinMessage(Message message, Long chatId) {
        Optional<Chat> optChat = this.chatRepository.findById(chatId);
        if(optChat.isPresent()) {
            Chat chat = optChat.get();
            chat.setPinnedMessage(message);
            this.chatRepository.save(chat);

            log.info("Pinned message: {}, on chat with id: {}", message.getId(), chatId);
            return message;
        }
        throw new RuntimeException("Cannot pin message");
    }

    @Override
    public void clearPinnedMessage(Long chatId) {
        Optional<Chat> optChat = this.chatRepository.findById(chatId);
        if(optChat.isPresent()) {
            Chat chat = optChat.get();
            chat.setPinnedMessage(null);
            this.chatRepository.save(chat);

            log.info("Cleared pinned message on chat with id: {}", chatId);
        }
    }

    @Override
    public Message addMessageToChat(Long chatId, Message message) {
        Optional<Chat> optChat = this.chatRepository.findById(chatId);
        if(optChat.isPresent()) {
            Chat chat = optChat.get();
            List<Message> messages = chat.getMessages();
            messages.add(message);
            chat.setMessages(messages);
            this.chatRepository.save(chat);

            log.info("Adding new message from sender: {}, to chat: {}", message.getSender().getName(), chatId);
            return message;
        }
        throw new RuntimeException("Cannot add message to chat");
    }

    @Override
    public List<Message> getAllMessagesFromChat(Long chatId) {
        Optional<Chat> optChat = this.chatRepository.findById(chatId);
        if(optChat.isPresent()) {
            log.info("Getting all messages from chat with id: {}", chatId);
            return optChat.get().getMessages();
        }
        throw new RuntimeException("Cannot return messages");
    }
}
