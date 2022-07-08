package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Message pinnedMessage;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    private Collection<Message> messages = new ArrayList<>();
}