package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.Chat;
import mk.ukim.finki.timskiproekt.model.Session;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.model.enums.SessionStatus;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SessionService {
    Session getSession(Long id);

    String getSessionStatus(Long id);

    List<Student> getAllowedStudentsBySession(Long id);

    List<Student> getAllStudentsBySession(Long id);

    Map<LocalDateTime, LocalDateTime> getSessionTimeSlot(Long id);

    Session addStudentInSession(Student student, Long sessionId);

    List<Student> getStudentsByStatus(StudentStatus status, Long sessionId);

    void editStatusForStudent(StudentStatus newStatus, Long studentId, Long sessionId);

    Chat getChatBySession(Long id);

    void endSession(Long id);

    void changeSessionStatus(Long id, SessionStatus newStatus);

    Session create(String name, String code, Long roomId);

    void delete(Long id);
}
