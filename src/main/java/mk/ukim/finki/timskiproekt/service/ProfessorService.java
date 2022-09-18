package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.Professor;

import java.util.List;

public interface ProfessorService {
    Professor getProfessor(Long id);

    List<Course> getAllCoursesForProfessor(Long professorId);

    Professor addCourseToProfessor(String courseCode, Long professorId);
}
