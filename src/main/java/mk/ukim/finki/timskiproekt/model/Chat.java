package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Message pinnedMessage = null;

    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

    @OneToOne
    private Session session;
}