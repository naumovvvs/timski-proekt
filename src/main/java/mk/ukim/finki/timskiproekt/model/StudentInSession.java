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
public class StudentInSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Session session;

    @ManyToOne
    private Student student;

    @Enumerated(value = EnumType.STRING)
    private StudentStatus status;

    private LocalDateTime enterTime;

    private LocalDateTime leaveTime;

    public StudentInSession(Session session, Student student) {
        this.session = session;
        this.student = student;
        this.status = StudentStatus.UNIDENTIFIED;
        this.enterTime = LocalDateTime.now();
    }
}