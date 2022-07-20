package mk.ukim.finki.timskiproekt.web.api;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.dto.CourseDTO;
import mk.ukim.finki.timskiproekt.model.dto.RoomToCourseDTO;
import mk.ukim.finki.timskiproekt.model.enums.Semester;
import mk.ukim.finki.timskiproekt.service.CourseService;
import mk.ukim.finki.timskiproekt.service.RoomService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/course")
@AllArgsConstructor
public class CourseRestController {

    private final CourseService courseService;
    private final RoomService roomService;
    private final ModelMapper modelMapper;

    @GetMapping("/name/{courseName}")
    public ResponseEntity<CourseDTO> getCourseByName(@PathVariable String courseName) {
        return Optional.of(this.courseService.getCourseByName(courseName))
                .map(course -> ResponseEntity.ok().body(modelMapper.map(course, CourseDTO.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{courseCode}")
    public ResponseEntity<CourseDTO> getCourseByCode(@PathVariable String courseCode) {
        return Optional.of(this.courseService.getCourseByCode(courseCode))
                .map(course -> ResponseEntity.ok().body(modelMapper.map(course, CourseDTO.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all-courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses(){
        List<CourseDTO> list = new ArrayList<>();
        this.courseService.getAllCourses().forEach(c -> list.add(modelMapper.map(c, CourseDTO.class)));

        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/all-courses-semester")
    public ResponseEntity<List<CourseDTO>> getAllCoursesBySemester(@RequestParam String semester){
        List<CourseDTO> list = new ArrayList<>();
        this.courseService.getAllCoursesBySemester(Semester.valueOf(semester))
                .forEach(c -> list.add(modelMapper.map(c, CourseDTO.class)));

        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/all-rooms/{courseCode}")
    public ResponseEntity<List<Room>> getAllRoomsByCourse(@PathVariable String courseCode) {
        return ResponseEntity.ok().body(this.courseService.getAllRoomsByCourse(courseCode));
    }

    @PostMapping("/create")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok().body(modelMapper.map(this.courseService.createCourse(courseDTO), CourseDTO.class));
    }

    @PostMapping("/delete/{courseName}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseName) {
        this.courseService.deleteCourseByName(courseName);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-room")
    public ResponseEntity<Room> addRoomToCourse(@RequestBody RoomToCourseDTO rcDTO) {
        Room room = this.roomService.getRoomById(rcDTO.getRoomId());
        return ResponseEntity.ok().body(this.courseService.addRoomToCourse(room, rcDTO.getCourseCode()));
    }

    @PostMapping("/delete-room")
    public ResponseEntity<?> deleteRoomFromCourse(@RequestBody RoomToCourseDTO rcDTO) {
        Room room = this.roomService.getRoomById(rcDTO.getRoomId());

        this.courseService.deleteRoomFromCourse(room, rcDTO.getCourseCode());

        return ResponseEntity.ok().build();
    }
}
