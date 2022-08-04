package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentInRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Room room;

    @ManyToOne
    private Student student;

    @Enumerated(value = EnumType.STRING)
    private StudentStatus status;

    private LocalDateTime enterTime;

    private LocalDateTime leaveTime;

    public StudentInRoom(Room room, Student student) {
        this.room = room;
        this.student = student;
        this.status = StudentStatus.UNIDENTIFIED;
        this.enterTime = LocalDateTime.now();
    }
}