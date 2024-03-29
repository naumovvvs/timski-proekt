package mk.ukim.finki.timskiproekt.web.api;

import lombok.AllArgsConstructor;
import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.*;
import mk.ukim.finki.timskiproekt.model.dto.projections.InterruptionInRoomReportDto;
import mk.ukim.finki.timskiproekt.model.dto.projections.MessageInRoomReportDto;
import mk.ukim.finki.timskiproekt.model.enums.RoomStatus;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;
import mk.ukim.finki.timskiproekt.model.dto.projections.StudentInRoomReportDto;
import mk.ukim.finki.timskiproekt.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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

    @GetMapping("/status/{id}")
    public String getStatus(@PathVariable Long id) {
        return this.roomService.getRoomStatus(id);
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

    @GetMapping("/open/{id}")
    public void startRoom(@PathVariable Long id) {
        this.roomService.openRoom(id);
    }


    @GetMapping("/allowed-timeslot/{id}")
    public ResponseEntity<Map<LocalDateTime, LocalDateTime>> getAllowedTimeSlot(@PathVariable Long id) {
        return Optional.of(this.roomService.getRoomAllowedTimeSlot(id))
                .map(timeMap -> ResponseEntity.ok().body(timeMap))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/timeslot/{id}")
    public ResponseEntity<Map<LocalDateTime, LocalDateTime>> getTimeSlot(@PathVariable Long id) {
        return Optional.of(this.roomService.getRoomInteractionTimeSlot(id))
                .map(timeMap -> ResponseEntity.ok().body(timeMap))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all-students/{id}")
    public List<Student> getAllStudents(@PathVariable Long id) {
        return this.roomService.getAllStudentsByRoom(id);
    }

    @GetMapping("/allowed-students/{id}")
    public List<Student> getAllowedStudents(@PathVariable Long id) {
        return this.roomService.getAllowedStudentsByRoom(id);
    }

    @GetMapping("/by-status/{id}/{status}")
    public List<Student> getStudentsByStatus(@PathVariable Long id, @PathVariable String status) {
        return this.roomService.getStudentsByStatus(StudentStatus.valueOf(status), id);
    }

    @PostMapping("/edit-student-status/{roomId}")
    public void editStudentStatus(@PathVariable Long roomId, @RequestBody EditStudentStatusDto studentStatusDto) {
        this.roomService.editStatusForStudent(roomId, studentStatusDto);
    }

    @GetMapping("/add-student/{roomId}/{studentId}")
    public ResponseEntity<Room> addStudent(@PathVariable Long roomId, @PathVariable Long studentId) {
        return Optional.of(this.roomService.addStudentInRoom(studentId, roomId))
                .map(session -> ResponseEntity.ok().body(session))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/leave-student/{roomId}/{studentId}")
    public void leaveRoom(@PathVariable Long roomId, @PathVariable Long studentId) {
        this.roomService.leaveRoomForStudent(roomId, studentId);
    }

    @GetMapping("/get-chat/{id}")
    public ResponseEntity<Chat> getChat(@PathVariable Long id) {
        return Optional.of(this.roomService.getChatByRoom(id))
                .map(chat -> ResponseEntity.ok().body(chat))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Secured("ROLE_PROFESSOR")
    @GetMapping("/end/{roomId}")
    public void endSession(@PathVariable Long roomId) {
        this.roomService.endRoom(roomId);
    }

    @GetMapping("/end/{roomId}/room-summary/{studentId}")
    public ResponseEntity<RoomSummaryDTO> getRoomSummary(@PathVariable Long roomId, @PathVariable Long studentId) {
        return Optional.of(ResponseEntity.ok().body(this.roomService.getRoomSummary(roomId, studentId)))
                .orElseThrow(() -> new RuntimeException("Room summary ERROR"));
    }

    @GetMapping("/change/{id}/{newStatus}")
    public void changeSessionStatus(@PathVariable Long id, @PathVariable String newStatus) {
        this.roomService.changeRoomStatus(id, RoomStatus.valueOf(newStatus));
    }

    @GetMapping("/moderator/{id}")
    public ResponseEntity<Professor> getModerator(@PathVariable Long id) {
        return Optional.of(this.roomService.getModeratorByRoom(id))
                .map(professor -> ResponseEntity.ok().body(professor))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/student/connection-interruption")
    public void registerInterruption(@RequestBody InterruptionDTO interruptionDTO) {
        this.roomService.addInterruptionToSession(interruptionDTO.getTime(), interruptionDTO.getTotalDuration(),
                interruptionDTO.getRoomId(), interruptionDTO.getStudentId());
    }

    @GetMapping("/report/{id}")
    public List<StudentInRoomReportDto> getRoomReport(@PathVariable Long id) {
        return this.roomService.getStudentInRoomReport(id);
    }

    @GetMapping("/msg-report/{id}")
    public List<MessageInRoomReportDto> getMessagesReport(@PathVariable Long id) {
        return this.roomService.getMessageInRoomReport(id);
    }

    @GetMapping("/interruption-report/{id}")
    public List<InterruptionInRoomReportDto> getInterruptionsReport(@PathVariable Long id) {
        return this.roomService.getInterruptionInRoomReport(id);
    }
}
