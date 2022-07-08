package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentInExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Exam exam;

    @ManyToOne
    private Student student;

    @Enumerated(value = EnumType.STRING)
    private StudentStatus status;
}