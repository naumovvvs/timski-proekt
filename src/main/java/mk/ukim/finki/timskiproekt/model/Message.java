package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime sentAt;

    @ManyToOne
    private Chat chat;

    @ManyToOne
    private AppUser sender;
}