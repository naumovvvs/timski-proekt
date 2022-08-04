package mk.ukim.finki.timskiproekt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.SaveMessageDto;
import mk.ukim.finki.timskiproekt.repository.*;
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
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Override
    public Chat getChat(Long id) {
        return this.chatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Chat with id: %d not found!", id)));
    }

    @Override
    public Chat createChatByRoomId(Long roomId) {
        Optional<Room> room = this.roomRepository.findById(roomId);
        if (room.isPresent()) {
            log.info("Creating chat for room with id: {}", roomId);
            return this.chatRepository.save(new Chat(null, null, new ArrayList<>()));
        }
        throw new RuntimeException(String.format("Room with id: %d not found!", roomId));
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
    public Message pinMessageById(Long messageId, Long chatId) {
        Optional<Chat> optChat = this.chatRepository.findById(chatId);
        Optional<Message> optMessage = this.messageRepository.findById(messageId);
        if(optChat.isPresent() && optMessage.isPresent()) {
            Chat chat = optChat.get();
            chat.setPinnedMessage(optMessage.get());
            this.chatRepository.save(chat);

            log.info("Pinned message: {}, on chat with id: {}", optMessage.get().getId(), chatId);
            return optMessage.get();
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
    public Message saveMessageToChatByRoom(Long roomId, SaveMessageDto messageDto) {
        Optional<Room> optRoom = this.roomRepository.findById(roomId);
        Optional<AppUser> optUser = this.userRepository.findById(messageDto.getSenderId());
        if(optRoom.isPresent() && optUser.isPresent()) {
            Chat chat = optRoom.get().getChat();
            Message message = new Message(messageDto.getContent(), chat, optUser.get());
            this.messageRepository.save(message);

            log.info("Saving new message from sender: {}, to chat: {}", message.getSender().getName(), chat.getId());
            return message;
        }
        throw new RuntimeException("Cannot save message to chat");
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
