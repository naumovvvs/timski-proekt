package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany
    private List<Interruption> interruptions;

    public StudentInRoom(Room room, Student student) {
        this.room = room;
        this.student = student;
        this.status = StudentStatus.UNIDENTIFIED;
        this.enterTime = LocalDateTime.now();
        this.interruptions = new ArrayList<>();
    }

    public void addNewInterruption(Interruption interruption) {
        this.interruptions.add(interruption);
    }
}