package mk.ukim.finki.timskiproekt.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import mk.ukim.finki.timskiproekt.model.enums.RoomStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String name;
    private LocalDateTime openFrom;
    private LocalDateTime openTo;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(value = EnumType.STRING)
    private RoomStatus status;

    @ManyToOne
    @JsonBackReference
    private Course course;

    /*
        During an exam one room has one moderator.
        Open for modifications, depending on business logic.
     */
    @JsonIgnore
    @ManyToOne
    private Professor moderator;

    @JsonIgnore
    @ManyToMany
    private List<Student> allowedStudents;

    @JsonIgnore
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<StudentInRoom> students;

    @OneToOne
    private Chat chat;

    public Room() {
        this.allowedStudents = new ArrayList<>();
        this.students = new ArrayList<>();
    }

    public Room(String name, LocalDateTime openFrom, LocalDateTime openTo, Course course, Professor moderator) {
        this.name = name;
        this.openFrom = openFrom;
        this.openTo = openTo;
        this.status = RoomStatus.CREATED;
        this.allowedStudents = new ArrayList<>();
        this.students = new ArrayList<>();
        this.course = course;
        this.moderator = moderator;
    }

    public Room(String name, LocalDateTime openFrom, LocalDateTime openTo, Course course, Professor moderator, Chat chat) {
        this.name = name;
        this.openFrom = openFrom;
        this.openTo = openTo;
        this.status = RoomStatus.CREATED;
        this.allowedStudents = new ArrayList<>();
        this.students = new ArrayList<>();
        this.course = course;
        this.moderator = moderator;
        this.chat = chat;
    }

    // TODO: only for testing purposes (delete later)
    public Room(String name, LocalDateTime openFrom, LocalDateTime openTo, Course course, Professor moderator, Chat chat,
                List<Student> allowedStudents) {
        this.name = name;
        this.openFrom = openFrom;
        this.openTo = openTo;
        this.status = RoomStatus.CREATED;
        this.allowedStudents = allowedStudents;
        this.students = new ArrayList<>();
        this.course = course;
        this.moderator = moderator;
        this.chat = chat;
    }
}