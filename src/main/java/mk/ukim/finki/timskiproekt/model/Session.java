package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.SessionStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String name;
    private String code;

    @Enumerated(value = EnumType.STRING)
    private SessionStatus status;

    @OneToOne
    private Room room;

    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
    private List<StudentInSession> students = new ArrayList<>();

    @OneToOne
    private Chat chat;
}