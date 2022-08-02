package mk.ukim.finki.timskiproekt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Message pinnedMessage = null;

    @JsonIgnore
    @OneToMany(mappedBy = "chat", fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();
}