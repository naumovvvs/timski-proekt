package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.StudentInExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInExamRepository extends JpaRepository<StudentInExam, Long> {
}