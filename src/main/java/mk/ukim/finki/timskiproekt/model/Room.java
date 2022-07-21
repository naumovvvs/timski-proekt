package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
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

    @OneToOne
    private Session session;

    @ManyToMany
    private List<Student> allowedStudents = new ArrayList<>();

    public Room(String name, LocalDateTime openFrom, LocalDateTime openTo, Course course, Professor moderator) {
        this.name = name;
        this.openFrom = openFrom;
        this.openTo = openTo;
        this.course = course;
        this.moderator = moderator;
    }
}