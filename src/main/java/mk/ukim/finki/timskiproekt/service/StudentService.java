package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.Student;

import java.util.List;

public interface StudentService {
    Student getStudentById(Long id);

    Student getStudentByIndex(Long index);

    Student addCourseToStudent(Course course, Long studentId);

    List<Course> getAllCoursesByStudent(Long id);
}
