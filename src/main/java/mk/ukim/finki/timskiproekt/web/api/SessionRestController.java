package mk.ukim.finki.timskiproekt.web.api;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.Chat;
import mk.ukim.finki.timskiproekt.model.Session;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.model.dto.EditStudentStatusDto;
import mk.ukim.finki.timskiproekt.model.dto.SaveSessionDto;
import mk.ukim.finki.timskiproekt.model.enums.SessionStatus;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;
import mk.ukim.finki.timskiproekt.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/session")
@AllArgsConstructor
public class SessionRestController {

    private final SessionService sessionService;

    @GetMapping("/{id}")
    public ResponseEntity<Session> findById(@PathVariable Long id) {
        return Optional.of(this.sessionService.getSession(id))
                .map(session -> ResponseEntity.ok().body(session))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{id}")
    public String getStatus(@PathVariable Long id) {
        return this.sessionService.getSessionStatus(id);
    }

    @PostMapping("/add")
    public ResponseEntity<Session> save(@RequestBody SaveSessionDto sessionDto) {
        return Optional.of(this.sessionService.create(sessionDto))
                .map(session -> ResponseEntity.ok().body(session))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteById(@PathVariable Long id) {
        this.sessionService.delete(id);
        try {
            this.sessionService.getSession(id);
        }
        catch (RuntimeException e) {
            // If the session can not be found, it was successfully deleted
            return ResponseEntity.ok().build();
        }
        // Otherwise
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/timeslot/{id}")
    public ResponseEntity<Map<LocalDateTime, LocalDateTime>> getTimeSlot(@PathVariable Long id) {
        return Optional.of(this.sessionService.getSessionTimeSlot(id))
                .map(timeMap -> ResponseEntity.ok().body(timeMap))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all-students/{id}")
    public List<Student> getAllStudents(@PathVariable Long id) {
        return this.sessionService.getAllStudentsBySession(id);
    }

    @GetMapping("/allowed-students/{id}")
    public List<Student> getAllowedStudents(@PathVariable Long id) {
        return this.sessionService.getAllowedStudentsBySession(id);
    }

    @GetMapping("/by-status/{id}/{status}")
    public List<Student> getStudentsByStatus(@PathVariable Long id, @PathVariable String status) {
        return this.sessionService.getStudentsByStatus(StudentStatus.valueOf(status), id);
    }

    @PutMapping("/edit-student-status/{id}")
    public void editStudentStatus(@PathVariable Long id, @RequestBody EditStudentStatusDto studentStatusDto) {
        this.sessionService.editStatusForStudent(id, studentStatusDto);
    }

    @GetMapping("/add-student/{id}/{studentId}")
    public ResponseEntity<Session> addStudent(@PathVariable Long id, @PathVariable Long studentId) {
        return Optional.of(this.sessionService.addStudentInSession(studentId, id))
                .map(session -> ResponseEntity.ok().body(session))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/get-chat/{id}")
    public ResponseEntity<Chat> getChat(@PathVariable Long id) {
        return Optional.of(this.sessionService.getChatBySession(id))
                .map(chat -> ResponseEntity.ok().body(chat))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/end-session/{id}")
    public void endSession(@PathVariable Long id) {
        this.sessionService.endSession(id);
    }

    @GetMapping("/change-session/{id}/{newStatus}")
    public void changeSessionStatus(@PathVariable Long id, @PathVariable String newStatus) {
        this.sessionService.changeSessionStatus(id, SessionStatus.valueOf(newStatus));
    }
}
