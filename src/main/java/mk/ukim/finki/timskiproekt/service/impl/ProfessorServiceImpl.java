package mk.ukim.finki.timskiproekt.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.Professor;
import mk.ukim.finki.timskiproekt.repository.CourseRepository;
import mk.ukim.finki.timskiproekt.repository.ProfessorRepository;
import mk.ukim.finki.timskiproekt.service.ProfessorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProfessorServiceImpl implements ProfessorService {

    private final ProfessorRepository professorRepository;
    private final CourseRepository courseRepository;

    @Override
    public Professor getProfessor(Long id) {
        log.info("Getting professor by id: {}", id);
        return (Professor) this.professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Professor with id: %d not found!", id)));
    }

    @Override
    public List<Course> getAllCoursesForProfessor(Long professorId) {
        log.info("Getting courses for professor by id: {}", professorId);
        return this.getProfessor(professorId).getCourses();
    }

    @Override
    public Professor addCourseToProfessor(String courseCode, Long professorId) {
        Professor prof = (Professor) this.professorRepository.findById(professorId)
                .orElseThrow(() -> new RuntimeException(String.format("Professor with id: %d not found!", professorId)));
        Course course = this.courseRepository.findByCode(courseCode);

        List<Course> profCourses = prof.getCourses();
        profCourses.add(course);
        prof.setCourses(profCourses);

        log.info("Assigning course with code: {}, to professor with id: {}", courseCode, professorId);
        return this.professorRepository.save(prof);
    }
}
