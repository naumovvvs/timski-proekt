package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private LocalDateTime openFrom;
    private LocalDateTime openTo;

    @ManyToOne
    private Course course;

    /*
        During an exam one room has one moderator.
        Open for modifications, depending on business logic.
     */
    @ManyToOne
    private Professor moderator;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private Collection<Exam> exams = new ArrayList<>();
}