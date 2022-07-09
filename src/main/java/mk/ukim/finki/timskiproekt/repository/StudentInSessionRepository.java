package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.StudentInSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInSessionRepository extends JpaRepository<StudentInSession, Long> {
}