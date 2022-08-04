package mk.ukim.finki.timskiproekt.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.EditRoomDto;
import mk.ukim.finki.timskiproekt.model.dto.EditStudentStatusDto;
import mk.ukim.finki.timskiproekt.model.dto.SaveRoomDto;
import mk.ukim.finki.timskiproekt.model.enums.RoomStatus;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;
import mk.ukim.finki.timskiproekt.repository.*;
import mk.ukim.finki.timskiproekt.service.RoomService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;
    private final ChatRepository chatRepository;
    private final StudentRepository studentRepository;
    private final StudentInRoomRepository studentInRoomRepository;

    @Override
    public Room getRoomById(Long id) {
        log.info("Getting room by id: {}", id);
        return this.roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", id)));
    }

    @Override
    public Room getRoomByName(String name) {
        log.info("Getting room by name: {}", name);
        return this.roomRepository.findByName(name);
    }

    @Override
    public String getRoomStatus(Long id) {
        log.info("Getting status for room with id: {}", id);
        return this.getRoomById(id).getStatus().name();
    }

    @Override
    public List<Room> getAllRoomsByCourse(Long id) {
        Course course = this.courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Course with id: %d not found!", id)));
        log.info("Getting all rooms by course id: {}", id);
        return this.roomRepository.findAllByCourse(course);
    }

    @Override
    public Professor getModeratorByRoom(Long id) {
        log.info("Getting moderator by room id: {}", id);
        return this.getRoomById(id).getModerator();
    }

    @Override
    public Map<LocalDateTime, LocalDateTime> getRoomAllowedTimeSlot(Long id) {
        Room room = this.getRoomById(id);
        HashMap<LocalDateTime, LocalDateTime> timeSlot = new HashMap<>();
        timeSlot.put(room.getOpenFrom(), room.getOpenTo());
        log.info("Getting allowed time slot by room id: {}", id);
        return timeSlot;
    }

    @Override
    public Map<LocalDateTime, LocalDateTime> getRoomInteractionTimeSlot(Long id) {
        Room room = this.getRoomById(id);
        HashMap<LocalDateTime, LocalDateTime> timeSlot = new HashMap<>();
        timeSlot.put(room.getStartTime(), room.getEndTime());
        log.info("Getting interaction time slot by room id: {}", id);
        return timeSlot;
    }

    @Override
    public List<Student> getAllowedStudentsByRoom(Long id) {
        log.info("Getting allowed students by room id: {}", id);
        return this.getRoomById(id).getAllowedStudents();
    }

    @Override
    public List<Student> getAllStudentsByRoom(Long id) {
        log.info("Getting all students for room with id: {}", id);
        return this.getRoomById(id).getStudents()
                .stream()
                .map(StudentInRoom::getStudent)
                .collect(Collectors.toList());
    }

    @Override
    public Room create(SaveRoomDto roomDto) {
        Course course = this.courseRepository.findById(roomDto.getCourseId())
                .orElseThrow(() -> new RuntimeException(String.format("Course with id: %d not found!", roomDto.getCourseId())));
        Professor moderator = (Professor) this.professorRepository.findById(roomDto.getModeratorId())
                .orElseThrow(() -> new RuntimeException(String.format("Moderator with id: %d not found!", roomDto.getModeratorId())));

        Chat chat = new Chat();
        this.chatRepository.save(chat);
        Room room = new Room(roomDto.getName(), roomDto.getOpenFrom(), roomDto.getOpenTo(), course, moderator, chat);
        log.info("Creating room for course with id: {}, by moderator with id: {}", roomDto.getCourseId(), roomDto.getModeratorId());
        return this.roomRepository.save(room);
    }

    @Override
    public Room update(Long id, EditRoomDto roomDto) {
        Room room = this.getRoomById(id);
        room.setName(roomDto.getName());
        room.setOpenFrom(roomDto.getOpenFrom());
        room.setOpenTo(roomDto.getOpenTo());
        log.info("Updating room by id: {}", id);
        return this.roomRepository.save(room);
    }

    @Override
    public void delete(Long id) {
        this.roomRepository.deleteById(id);
        log.info("Deleted room with id: {}", id);
    }

    @Override
    public Room addStudentInRoom(Long studentId, Long roomId) {
        Room room = this.roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
        // check if the student is allowed in the room
        //  if (room.getAllowedStudents().stream().anyMatch(student -> student.getId().equals(studentId))) {
        Optional<AppUser> appUser = this.studentRepository.findById(studentId);
        if (appUser.isPresent()) {
            StudentInRoom studentInRoom = new StudentInRoom(room, (Student) appUser.get());
                /*
                    TODO:
                     Test if the newly created object (studentInRoom) is saved in its own table,
                     when saving it in the container (room).

                     Update: must be saved in it's own table first.
                */
            this.studentInRoomRepository.save(studentInRoom);

            room.getStudents().add(studentInRoom);
            log.info("Adding student with id: {}, in room with id: {}", studentId, roomId);
            return this.roomRepository.save(room);
        }
        //}
        throw new RuntimeException(String.format("Student with id: %d can't join room with id: %d!",
                studentId, roomId));
    }

    @Override
    public List<Student> getStudentsByStatus(StudentStatus status, Long roomId) {
        log.info("Getting student with status: {}, in room with id: {}", status, roomId);
        return this.getRoomById(roomId).getStudents()
                .stream()
                .filter(s -> s.getStatus().equals(status))
                .map(StudentInRoom::getStudent)
                .collect(Collectors.toList());
    }

    @Override
    public void editStatusForStudent(Long roomId, EditStudentStatusDto studentStatusDto) {
        Room room = this.roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
        room.getStudents()
                .stream()
                .filter(s -> s.getStudent().getId().equals(studentStatusDto.getStudentId()))
                .findFirst()
                .ifPresent(ss -> ss.setStatus(StudentStatus.valueOf(studentStatusDto.getNewStudentStatus())));
        log.info("Changing student status to: {}, by id {}, in room with id: {}",
                studentStatusDto.getNewStudentStatus(), studentStatusDto.getStudentId(), roomId);
        this.roomRepository.save(room);
    }

    @Override
    public void leaveRoomForStudent(Long roomId, Long studentId) {
        Room room = this.roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
        Optional<AppUser> appUser = this.studentRepository.findById(studentId);
        if(appUser.isPresent()) {
            if (appUser.get().getRoles().stream().allMatch(role -> role.getName().equals("ROLE_STUDENT"))) {
                StudentInRoom studentInRoom = this.studentInRoomRepository
                        .findStudentInRoomByRoomAndStudent(room, (Student) appUser.get());
                studentInRoom.setLeaveTime(LocalDateTime.now());
                this.studentInRoomRepository.save(studentInRoom);
            }
        }
    }

    @Override
    public Chat getChatByRoom(Long id) {
        log.info("Getting chat by room id: {}", id);
        return this.getRoomById(id).getChat();
    }

    @Override
    public void endRoom(Long roomId) {
        Room room = this.roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
        room.setEndTime(LocalDateTime.now());
        room.setStatus(RoomStatus.CLOSED);
        log.info("Ending room with id: {}", roomId);
        this.roomRepository.save(room);
    }

    @Override
    public void openRoom(Long roomId) {
        Room room = this.roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
        room.setStartTime(LocalDateTime.now());
        room.setStatus(RoomStatus.OPEN);
        log.info("Opening room with id: {}", roomId);
        this.roomRepository.save(room);
    }

    @Override
    public void changeRoomStatus(Long id, RoomStatus newStatus) {
        Room room = this.getRoomById(id);
        room.setStatus(newStatus);
        log.info("Changing room status to: {}, with id: {}", newStatus, id);
        this.roomRepository.save(room);
    }
}
