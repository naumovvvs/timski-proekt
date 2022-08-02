package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Session;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.model.StudentInSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInSessionRepository extends JpaRepository<StudentInSession, Long> {
    StudentInSession findStudentInSessionBySessionAndStudent(Session session, Student student);
}