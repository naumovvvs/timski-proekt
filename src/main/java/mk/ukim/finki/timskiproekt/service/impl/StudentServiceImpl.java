package mk.ukim.finki.timskiproekt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.repository.CourseRepository;
import mk.ukim.finki.timskiproekt.repository.StudentRepository;
import mk.ukim.finki.timskiproekt.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Override
    public Student getStudentById(Long id) {
        log.info("Getting student by id: {}", id);
        return (Student) this.studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", id)));
    }

    @Override
    public Student getStudentByIndex(Long index) {
        log.info("Getting student by index: {}", index);
        return this.studentRepository.getByIndex(index);
    }

    @Override
    public Student addCourseToStudent(Course course, Long studentId) {
        Student student = this.getStudentById(studentId);
        student.getCourses().add(course);
        this.studentRepository.save(student);
        course.getStudents().add(student);
        this.courseRepository.save(course);
        log.info("Adding course with id: {}, to student with id: {}", course.getId(), studentId);
        return student;
    }

    @Override
    public List<Course> getAllCoursesByStudent(Long id) {
        log.info("Getting courses for student with id: {}", id);
        return this.getStudentById(id).getCourses();
    }

    @Override
    public List<Student> getAllStudents() {
        return this.studentRepository.findAll().stream()
                .filter(s -> s.getRoles().get(0).getName().equals("ROLE_STUDENT"))
                .map(appUser -> (Student) appUser)
                .collect(Collectors.toList());
    }
}
