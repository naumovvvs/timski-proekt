package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RoomService {
    Room getRoomById(Long id);

    Room getRoomByName(String name);

    List<Room> getAllRoomsByCourse(Long id);

    Session getSessionByRoom(Long id);

    Professor getModeratorByRoom(Long id);

    void addSessionToRoom(Session session, Long roomId);

    Map<LocalDateTime, LocalDateTime> getRoomTimeSlot(Long id);

    List<Student> getAllowedStudentsByRoom(Long id);

    Room create(String name, LocalDateTime openFrom, LocalDateTime openTo, Long courseId, Long moderatorId);

    void delete(Long id);
}
