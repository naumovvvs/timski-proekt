package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Student;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends UserRepository {
    Student getByIndex(Long index);
}