package mk.ukim.finki.timskiproekt.web.api;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.Chat;
import mk.ukim.finki.timskiproekt.model.Message;
import mk.ukim.finki.timskiproekt.model.dto.SaveMessageDto;
import mk.ukim.finki.timskiproekt.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/{id}")
    public ResponseEntity<Chat> findById(@PathVariable Long id) {
        return Optional.of(this.chatService.getChat(id))
                .map(chat -> ResponseEntity.ok().body(chat))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/messages/{id}")
    private List<Message> getAllMessages(@PathVariable Long id) {
        return this.chatService.getAllMessagesFromChat(id);
    }

    @PostMapping("/add/{sessionId}")
    public ResponseEntity<Chat> save(@PathVariable Long sessionId) {
        return Optional.of(this.chatService.createChatBySessionId(sessionId))
                .map(chat -> ResponseEntity.ok().body(chat))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping("/pinned-msg/{id}")
    public ResponseEntity<Message> getPinnedMessage(@PathVariable Long id) {
        return Optional.of(this.chatService.getPinnedMessageByChat(id))
                .map(message -> ResponseEntity.ok().body(message))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/clear/{id}")
    public void clearChat(@PathVariable Long id) {
        this.chatService.clearChat(id);
    }

    @GetMapping("/pin-msg/{id}/{msgId}")
    public ResponseEntity<Message> pinMessage(@PathVariable Long id, @PathVariable Long msgId) {
        return Optional.of(this.chatService.pinMessageById(id, msgId))
                .map(message -> ResponseEntity.ok().body(message))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/unpin-msg/{id}")
    public void unpinMessage(@PathVariable Long id) {
        this.chatService.clearPinnedMessage(id);
    }

    @PostMapping("/save-msg/{roomId}")
    public ResponseEntity<Message> saveMessage(@PathVariable Long roomId, @RequestBody SaveMessageDto messageDto) {
        return Optional.of(this.chatService.saveMessageToChatByRoom(roomId, messageDto))
                .map(message -> ResponseEntity.ok().body(message))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
