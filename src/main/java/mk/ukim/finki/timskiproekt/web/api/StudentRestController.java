package mk.ukim.finki.timskiproekt.web.api;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.dto.CourseDTO;
import mk.ukim.finki.timskiproekt.model.dto.CourseToUserDTO;
import mk.ukim.finki.timskiproekt.model.dto.StudentDTO;
import mk.ukim.finki.timskiproekt.service.CourseService;
import mk.ukim.finki.timskiproekt.service.StudentService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/student")
@AllArgsConstructor
public class StudentRestController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final ModelMapper modelMapper;

    @GetMapping("/id/{idNo}")
    public ResponseEntity<StudentDTO> getStudentById(@PathVariable Long idNo) {
        return ResponseEntity.ok().body(modelMapper.map(studentService.getStudentById(idNo), StudentDTO.class));
    }

    @GetMapping("/index/{ind}")
    public ResponseEntity<StudentDTO> getStudentByIndex(@PathVariable Long ind) {
        return ResponseEntity.ok().body(modelMapper.map(studentService.getStudentByIndex(ind), StudentDTO.class));
    }

    @PostMapping("/add-course")
    public ResponseEntity<StudentDTO> addCourseToStudent(@RequestBody CourseToUserDTO csDto) {
        Course course = courseService.getCourseByName(csDto.getCourseName());
        return ResponseEntity.ok().body(modelMapper.map(studentService.addCourseToStudent(course, csDto.getUserId()),
                StudentDTO.class));
    }

    @GetMapping("/all-courses/{studentId}")
    public ResponseEntity<List<CourseDTO>> getAllCoursesByStudent(@PathVariable Long studentId) {
        List<CourseDTO> courseDTOS = new ArrayList<>();
        studentService.getAllCoursesByStudent(studentId).forEach(c -> courseDTOS.add(modelMapper.map(c, CourseDTO.class)));

        return ResponseEntity.ok().body(courseDTOS);
    }
}
