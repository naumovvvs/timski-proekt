package mk.ukim.finki.timskiproekt.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.EditRoomDto;
import mk.ukim.finki.timskiproekt.model.dto.EditStudentStatusDto;
import mk.ukim.finki.timskiproekt.model.dto.RoomSummaryDTO;
import mk.ukim.finki.timskiproekt.model.dto.SaveRoomDto;
import mk.ukim.finki.timskiproekt.model.enums.RoomStatus;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;
import mk.ukim.finki.timskiproekt.repository.*;
import mk.ukim.finki.timskiproekt.service.RoomService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final InterruptionRepository interruptionRepository;

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

        List<Student> allowedStudents = new ArrayList<>();
        for (String index : roomDto.getAllowedStudents()) {
            Student student = this.studentRepository.getByIndex(Long.parseLong(index));
            allowedStudents.add(student);
        }

        Room room = new Room(roomDto.getName(), roomDto.getOpenFrom(), roomDto.getOpenTo(), course, moderator, chat, allowedStudents);
        this.roomRepository.save(room);

        course.getRooms().add(room);
        this.courseRepository.save(course);

        log.info("Creating room for course with id: {}, by moderator with id: {}", roomDto.getCourseId(), roomDto.getModeratorId());
        return room;
    }

    // TODO: only for testing purposes (delete later) (maybe useful because of allowed students?)
//    @Override
//    public Room create(SaveRoomDto roomDto, List<Student> allowed) {
//        Course course = this.courseRepository.findById(roomDto.getCourseId())
//                .orElseThrow(() -> new RuntimeException(String.format("Course with id: %d not found!", roomDto.getCourseId())));
//        Professor moderator = (Professor) this.professorRepository.findById(roomDto.getModeratorId())
//                .orElseThrow(() -> new RuntimeException(String.format("Moderator with id: %d not found!", roomDto.getModeratorId())));
//
//        Chat chat = new Chat();
//        this.chatRepository.save(chat);
//        Room room = new Room(roomDto.getName(), roomDto.getOpenFrom(), roomDto.getOpenTo(), course, moderator, chat, allowed);
//        log.info("Creating room for course with id: {}, by moderator with id: {}", roomDto.getCourseId(), roomDto.getModeratorId());
//        return this.roomRepository.save(room);
//    }

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
            // Check if there is previous record for the same student and room without leave time
            // If so, update the record and set the leave time as current time
            Optional<StudentInRoom> previousOpt = this.studentInRoomRepository
                    .findAllByRoomAndStudent(room, (Student) appUser.get())
                    .stream().filter(x->x.getLeaveTime()==null).findFirst();

            // check if there is previous record without recorded leave time
            if(previousOpt.isPresent()) {
                StudentInRoom previousRecord = previousOpt.get();
                previousRecord.setLeaveTime(LocalDateTime.now());
                this.studentInRoomRepository.save(previousRecord);
            }

            StudentInRoom studentInRoom = new StudentInRoom(room, (Student) appUser.get());
            // save the object first in it's own table
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
        Optional<StudentInRoom> optionalStudent = room.getStudents()
                .stream()
                .filter(s -> s.getStudent().getId().equals(studentStatusDto.getStudentId()))
                .max(Comparator.comparing(StudentInRoom::getEnterTime));

        if (optionalStudent.isPresent()) {
            StudentInRoom student = optionalStudent.get();
            student.setStatus(StudentStatus.valueOf(studentStatusDto.getNewStudentStatus()));
            // if the moderator blocked the student, remove it from the list of allowed students for the room
            if (student.getStatus().equals(StudentStatus.BLOCKED)) {
                room.getAllowedStudents().remove(student.getStudent());
            }
            this.studentInRoomRepository.save(student);
        }
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
            if (appUser.get().getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_STUDENT"))) {
                Optional<StudentInRoom> optStudentInRoom = this.studentInRoomRepository
                        .findAllByRoomAndStudent(room, (Student) appUser.get())
                        .stream()
                        .filter(x->x.getLeaveTime()==null)
                        .findFirst();

                if(!optStudentInRoom.isPresent()) {
                    throw new RuntimeException("Cannot find student in room object!");
                }

                StudentInRoom studentInRoom = optStudentInRoom.get();
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

    @Override
    public boolean checkIfStudentIsAllowed(Room room, Long studentId) {
        Optional<AppUser> optionalStudent = this.studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            return room.getAllowedStudents().stream()
                    .anyMatch(s -> s.getId().equals(studentId));
        }
        return false;
    }

    @Override
    public void addInterruptionToSession(String time, int totalDuration, Long roomId, Long studentId) {
        log.info("Record interruption at: " + time + ", in room: " + roomId + ", by student: " + studentId);
        String dateTimePattern = "M/d/yyyy, h:m:s a";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);

        LocalDateTime dateTime = LocalDateTime.parse(time, formatter);

        Room room = this.roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
        Optional<AppUser> appUser = this.studentRepository.findById(studentId);

        if(appUser.isPresent()) {
            StudentInRoom studentInRoom = this.studentInRoomRepository
                    .findAllByRoomAndStudent(room, (Student) appUser.get())
                    .stream()
                    .filter(x->x.getLeaveTime()==null)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Cannot find student in room!"));

            Interruption interruption = this.interruptionRepository.save(new Interruption(dateTime, totalDuration));
            studentInRoom.addNewInterruption(interruption);
            this.studentInRoomRepository.save(studentInRoom);
        }
    }

    @Override
    public RoomSummaryDTO getRoomSummary(Long roomId, Long studentId) {
        Room room = this.roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
        Optional<AppUser> appUser = this.studentRepository.findById(studentId);

        if(appUser.isPresent()) {
            List<StudentInRoom> studentInRoomList = this.studentInRoomRepository
                    .findAllByRoomAndStudent(room, (Student) appUser.get());

            Iterable<Long> interruptionsIds = studentInRoomList.stream()
                    .flatMap(x->x.getInterruptions().stream())
                    .map(Interruption::getId)
                    .collect(Collectors.toList());

            List<Interruption> studentInterruptions = this.interruptionRepository.findAllById(interruptionsIds);
            int totalInterruptions = studentInterruptions.size();
            int interruptionsDuration = studentInterruptions
                    .stream()
                    .mapToInt(Interruption::getTotalDurationSeconds)
                    .sum();

            Student student = (Student) appUser.get();
            Duration timeElapsed = Duration.between(room.getStartTime(), room.getEndTime());
            String studentFullName = student.getName()
                    + " (" + student.getIndex() + ")";

            StudentInRoom studentInRoom = studentInRoomList.stream()
                    .max(Comparator.comparing(StudentInRoom::getLeaveTime))
                    .orElseThrow(() -> new RuntimeException("Cannot find student in room!"));

            return new RoomSummaryDTO(room.getName(), timeElapsed.toMinutes(), studentFullName,
                    totalInterruptions, interruptionsDuration, studentInRoom.getStatus().name());
        } else {
            throw new RuntimeException("App user not found");
        }
    }
}
