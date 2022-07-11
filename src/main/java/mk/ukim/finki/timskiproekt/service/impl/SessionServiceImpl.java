package mk.ukim.finki.timskiproekt.service.impl;

import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.enums.SessionStatus;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;
import mk.ukim.finki.timskiproekt.repository.RoomRepository;
import mk.ukim.finki.timskiproekt.repository.SessionsRepository;
import mk.ukim.finki.timskiproekt.service.SessionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionsRepository sessionRepository;
    private final RoomRepository roomRepository;

    public SessionServiceImpl(SessionsRepository sessionRepository, RoomRepository roomRepository) {
        this.sessionRepository = sessionRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Session getSession(Long id) {
        log.info("Getting session by id: {}", id);
        return this.sessionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Session with id: %d not found!", id)));
    }

    @Override
    public String getSessionStatus(Long id) {
        log.info("Getting status for session with id: {}", id);
        return this.getSession(id).getStatus().name();
    }

    @Override
    public List<Student> getAllowedStudentsBySession(Long id) {
        log.info("Getting allowed students for session with id: {}", id);
        return this.getSession(id).getRoom().getAllowedStudents();
    }

    @Override
    public List<Student> getAllStudentsBySession(Long id) {
        log.info("Getting all students for session with id: {}", id);
        return this.getSession(id).getStudents()
                .stream()
                .map(StudentInSession::getStudent)
                .collect(Collectors.toList());
    }

    @Override
    public Map<LocalDateTime, LocalDateTime> getSessionTimeSlot(Long id) {
        Session session = this.getSession(id);
        HashMap<LocalDateTime, LocalDateTime> timeSlot = new HashMap<>();
        timeSlot.put(session.getStartTime(), session.getEndTime());
        log.info("Getting time slot by session id: {}", id);
        return timeSlot;
    }

    @Override
    public Session addStudentInSession(Student student, Long sessionId) {
        Session session = this.getSession(sessionId);
        StudentInSession studentInSession = new StudentInSession(session, student);
        session.getStudents().add(studentInSession);
        /*
            TODO:
             Test if the newly created object (studentInSession) is saved in its own table,
             when saving it in the container (session).
        */
        log.info("Adding student with id: {}, in session with id: {}", student.getId(), sessionId);
        return this.sessionRepository.save(session);
    }

    @Override
    public List<Student> getStudentsByStatus(StudentStatus status, Long sessionId) {
        log.info("Getting student with status: {}, in session with id: {}", status, sessionId);
        return this.getSession(sessionId).getStudents()
                .stream()
                .filter(s -> s.getStatus().equals(status))
                .map(StudentInSession::getStudent)
                .collect(Collectors.toList());
    }

    @Override
    public void editStatusForStudent(StudentStatus newStatus, Long studentId, Long sessionId) {
        Session session = this.getSession(sessionId);
        session.getStudents()
                .stream()
                .filter(s -> s.getStudent().getId().equals(studentId))
                .findFirst()
                .ifPresent(ss -> ss.setStatus(newStatus));
        log.info("Changing student status to: {}, by id {}, in session with id: {}", newStatus, studentId, sessionId);
        this.sessionRepository.save(session);
    }

    @Override
    public Chat getChatBySession(Long id) {
        log.info("Getting chat by session id: {}", id);
        return this.getSession(id).getChat();
    }

    @Override
    public void endSession(Long id) {
        Session session = this.getSession(id);
        session.setStatus(SessionStatus.CLOSED);
        session.setEndTime(LocalDateTime.now());
        log.info("Ending session with id: {}", id);
        this.sessionRepository.save(session);
    }

    @Override
    public void changeSessionStatus(Long id, SessionStatus newStatus) {
        Session session = this.getSession(id);
        session.setStatus(newStatus);
        log.info("Changing session status to: {}, with id: {}", newStatus, id);
        this.sessionRepository.save(session);
    }

    @Override
    public Session create(String name, String code, Long roomId) {
        Room room = this.roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException(String.format("Room with id: %d not found!", roomId)));
        Session session = new Session(name, code, room);
        log.info("Creating session for room with id: {}", roomId);
        return this.sessionRepository.save(session);
    }

    @Override
    public void delete(Long id) {
        this.sessionRepository.deleteById(id);
        log.info("Deleted room with id: {}", id);
    }
}
