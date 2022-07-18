package mk.ukim.finki.timskiproekt.web.api;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.Professor;
import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.Session;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.model.dto.EditRoomDto;
import mk.ukim.finki.timskiproekt.model.dto.SaveRoomDto;
import mk.ukim.finki.timskiproekt.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/room")
@AllArgsConstructor
public class RoomRestController {

    private final RoomService roomService;

    @GetMapping("/{courseId}")
    private List<Room> findAllByCourse(@PathVariable Long courseId) {
        return this.roomService.getAllRoomsByCourse(courseId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findById(@PathVariable Long id) {
        return Optional.of(this.roomService.getRoomById(id))
                .map(room -> ResponseEntity.ok().body(room))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Room> findByName(@PathVariable String name) {
        return Optional.of(this.roomService.getRoomByName(name))
                .map(room -> ResponseEntity.ok().body(room))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/session")
    public ResponseEntity<Session> findSessionByRoom(@PathVariable Long id) {
        return Optional.of(this.roomService.getSessionByRoom(id))
                .map(session -> ResponseEntity.ok().body(session))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public ResponseEntity<Room> save(@RequestBody SaveRoomDto roomDto) {
        return Optional.of(this.roomService.create(roomDto))
                .map(room -> ResponseEntity.ok().body(room))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Room> edit(@PathVariable Long id, @RequestBody EditRoomDto roomDto) {
        return Optional.of(this.roomService.update(id, roomDto))
                .map(room -> ResponseEntity.ok().body(room))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        this.roomService.delete(id);
        try {
            this.roomService.getRoomById(id);
        }
        catch (RuntimeException e) {
            // If the room can not be found, it was successfully deleted
            return ResponseEntity.ok().build();
        }
        // Otherwise
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/timeslot/{id}")
    public ResponseEntity<Map<LocalDateTime, LocalDateTime>> getTimeSlot(@PathVariable Long id) {
        return Optional.of(this.roomService.getRoomTimeSlot(id))
                .map(timeMap -> ResponseEntity.ok().body(timeMap))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/students/{id}")
    public List<Student> getAllowedStudents(@PathVariable Long id) {
        return this.roomService.getAllowedStudentsByRoom(id);
    }

    @GetMapping("/moderator/{id}")
    public ResponseEntity<Professor> getModerator(@PathVariable Long id) {
        return Optional.of(this.roomService.getModeratorByRoom(id))
                .map(professor -> ResponseEntity.ok().body(professor))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
