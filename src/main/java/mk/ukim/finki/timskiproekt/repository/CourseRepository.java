package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}