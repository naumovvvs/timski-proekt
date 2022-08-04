//package mk.ukim.finki.timskiproekt.service.impl;
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import mk.ukim.finki.timskiproekt.model.*;
//import mk.ukim.finki.timskiproekt.model.dto.EditStudentStatusDto;
//import mk.ukim.finki.timskiproekt.model.enums.RoomStatus;
//import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;
//import mk.ukim.finki.timskiproekt.repository.*;
//import mk.ukim.finki.timskiproekt.service.SessionService;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//@AllArgsConstructor
//public class SessionServiceImpl implements SessionService {
//
//    private final SessionsRepository sessionRepository;
//    private final RoomRepository roomRepository;
//    private final StudentRepository studentRepository;
//    private final StudentInRoomRepository studentInRoomRepository;
//    private final ChatRepository chatRepository;
//
//    @Override
//    public Session getSession(Long id) {
//        log.info("Getting session by id: {}", id);
//        return this.sessionRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException(String.format("Session with id: %d not found!", id)));
//    }
//
//    @Override
//    public String getSessionStatus(Long id) {
//        log.info("Getting status for session with id: {}", id);
//        return this.getSession(id).getStatus().name();
//    }
//
//    @Override
//    public List<Student> getAllowedStudentsBySession(Long id) {
//        Session session = this.getSession(id);
//        log.info("Getting allowed students for session with id: {}", id);
//        return this.roomRepository.findBySession(session).getAllowedStudents();
//    }
//
//    @Override
//    public List<Student> getAllStudentsBySession(Long id) {
//        log.info("Getting all students for session with id: {}", id);
//        return this.getSession(id).getStudents()
//                .stream()
//                .map(StudentInRoom::getStudent)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public Map<LocalDateTime, LocalDateTime> getSessionTimeSlot(Long id) {
//        Session session = this.getSession(id);
//        HashMap<LocalDateTime, LocalDateTime> timeSlot = new HashMap<>();
//        timeSlot.put(session.getStartTime(), session.getEndTime());
//        log.info("Getting time slot by session id: {}", id);
//        return timeSlot;
//    }
//
//    @Override
//    public Session addStudentInSessionByRoom(Long studentId, Long roomId) {
//        // get the room associated with this session
//        Room room = this.roomRepository.findById(roomId)
//                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
//        // check if the student is allowed in the room
//        //  if (room.getAllowedStudents().stream().anyMatch(student -> student.getId().equals(studentId))) {
//            Optional<AppUser> appUser = this.studentRepository.findById(studentId);
//            if (appUser.isPresent()) {
//                StudentInRoom studentInRoom = new StudentInRoom(room, (Student) appUser.get());
//                /*
//                    TODO:
//                     Test if the newly created object (studentInRoom) is saved in its own table,
//                     when saving it in the container (session).
//
//                     Update: must be saved in it's own table first.
//                */
//                this.studentInRoomRepository.save(studentInRoom);
//
//                room.getStudents().add(studentInRoom);
//                log.info("Adding student with id: {}, in session with id: {}", studentId, session.getId());
//                return this.roomRepository.save(room);
//            }
//        //}
//        throw new RuntimeException(String.format("Student with id: %d can't join session with id: %d!",
//                studentId, session.getId()));
//    }
//
//    @Override
//    public List<Student> getStudentsByStatus(StudentStatus status, Long sessionId) {
//        log.info("Getting student with status: {}, in session with id: {}", status, sessionId);
//        return this.getSession(sessionId).getStudents()
//                .stream()
//                .filter(s -> s.getStatus().equals(status))
//                .map(StudentInRoom::getStudent)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public void editStatusForStudent(Long roomId, EditStudentStatusDto studentStatusDto) {
//        Room room = this.roomRepository.findById(roomId)
//                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
//        Session session = room.getSession();
//        session.getStudents()
//                .stream()
//                .filter(s -> s.getStudent().getId().equals(studentStatusDto.getStudentId()))
//                .findFirst()
//                .ifPresent(ss -> ss.setStatus(StudentStatus.valueOf(studentStatusDto.getNewStudentStatus())));
//        log.info("Changing student status to: {}, by id {}, in session with id: {}",
//                studentStatusDto.getNewStudentStatus(), studentStatusDto.getStudentId(), session.getId());
//        this.sessionRepository.save(session);
//    }
//
//    @Override
//    public void leaveSessionForStudentByRoom(Long studentId, Long roomId) {
//        Room room = this.roomRepository.findById(roomId)
//                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
//        Optional<AppUser> appUser = this.studentRepository.findById(studentId);
//        if(appUser.isPresent()) {
//            if (appUser.get().getRoles().stream().allMatch(role -> role.getName().equals("ROLE_STUDENT"))) {
//                StudentInRoom studentInRoom = this.studentInRoomRepository
//                        .findStudentInSessionBySessionAndStudent(room.getSession(), (Student) appUser.get());
//                studentInRoom.setLeaveTime(LocalDateTime.now());
//                this.studentInRoomRepository.save(studentInRoom);
//            }
//        }
//    }
//
//    @Override
//    public Chat getChatBySession(Long id) {
//        log.info("Getting chat by session id: {}", id);
//        return this.getSession(id).getChat();
//    }
//
//    @Override
//    public void endSessionByRoom(Long roomId) {
//        Room room = this.roomRepository.findById(roomId)
//                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
//        Session session = room.getSession();
//        session.setStatus(RoomStatus.CLOSED);
//        session.setEndTime(LocalDateTime.now());
//        log.info("Ending session with id: {}", session.getId());
//        this.sessionRepository.save(session);
//    }
//
//    @Override
//    public void changeSessionStatus(Long id, RoomStatus newStatus) {
//        Session session = this.getSession(id);
//        session.setStatus(newStatus);
//        log.info("Changing session status to: {}, with id: {}", newStatus, id);
//        this.sessionRepository.save(session);
//    }
//
//    @Override
//    public Session create(Long roomId) {
//        Room room = this.roomRepository.findById(roomId)
//                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
//        log.info("Creating session for room with id: {}", room.getId());
//        Chat chat = new Chat();
//        this.chatRepository.save(chat);
//        Session session = new Session(chat);
//        this.sessionRepository.save(session);
//        room.setSession(session);
//        this.roomRepository.save(room);
//        return session;
//    }
//
//    @Override
//    public void delete(Long id) {
//        this.sessionRepository.deleteById(id);
//        log.info("Deleted room with id: {}", id);
//    }
//}
