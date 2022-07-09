package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.enums.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Course findByName(String name);
    Course findByCode(String code);
    List<Course> findAllBySemester(Semester semester);
    void deleteByName(String name);
}