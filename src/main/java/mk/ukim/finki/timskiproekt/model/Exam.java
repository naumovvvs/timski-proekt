package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.ExamStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String name;
    private String code;

    @Enumerated(value = EnumType.STRING)
    private ExamStatus status;

    @ManyToOne
    private Room room;

    @OneToMany(mappedBy = "exam", fetch = FetchType.LAZY)
    private Collection<StudentInExam> students = new ArrayList<>();

    @OneToOne
    private Chat chat;
}