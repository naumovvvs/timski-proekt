package mk.ukim.finki.timskiproekt.web.api;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.dto.CourseDTO;
import mk.ukim.finki.timskiproekt.model.dto.CourseToUserDTO;
import mk.ukim.finki.timskiproekt.model.dto.StudentDTO;
import mk.ukim.finki.timskiproekt.service.CourseService;
import mk.ukim.finki.timskiproekt.service.ProfessorService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/professor")
@AllArgsConstructor
public class ProfessorRestController {

    private final ProfessorService professorService;
    private final CourseService courseService;
    private final ModelMapper modelMapper;

    @GetMapping("/all-courses/{professorId}")
    public ResponseEntity<List<CourseDTO>> getAllCoursesForProfessor(@PathVariable Long professorId) {
        List<CourseDTO> courseDTOS = new ArrayList<>();
        professorService.getAllCoursesForProfessor(professorId)
                .forEach(c -> courseDTOS.add(modelMapper.map(c, CourseDTO.class)));

        return ResponseEntity.ok().body(courseDTOS);
    }

    @PostMapping("/add-course")
    public ResponseEntity<StudentDTO> addCourseToStudent(@RequestBody CourseToUserDTO csDto) {
        Course course = courseService.getCourseByName(csDto.getCourseName());
        return ResponseEntity.ok().body(modelMapper.map(professorService.addCourseToProfessor(course.getCode(), csDto.getUserId()),
                StudentDTO.class));
    }
}
