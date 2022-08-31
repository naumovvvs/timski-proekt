package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.EditRoomDto;
import mk.ukim.finki.timskiproekt.model.dto.EditStudentStatusDto;
import mk.ukim.finki.timskiproekt.model.dto.RoomSummaryDTO;
import mk.ukim.finki.timskiproekt.model.dto.SaveRoomDto;
import mk.ukim.finki.timskiproekt.model.enums.RoomStatus;
import mk.ukim.finki.timskiproekt.model.enums.StudentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RoomService {
    Room getRoomById(Long id);

    Room getRoomByName(String name);

    String getRoomStatus(Long id);

    List<Room> getAllRoomsByCourse(Long id);

    Professor getModeratorByRoom(Long id);

    Map<LocalDateTime, LocalDateTime> getRoomAllowedTimeSlot(Long id);

    Map<LocalDateTime, LocalDateTime> getRoomInteractionTimeSlot(Long id);

    List<Student> getAllowedStudentsByRoom(Long id);

    List<Student> getAllStudentsByRoom(Long id);

    Room create(SaveRoomDto roomDto);

    // TODO: only for testing purposes (delete later)
    //Room create(SaveRoomDto roomDto, List<Student> allowed);

    Room update(Long id, EditRoomDto roomDto);

    void delete(Long id);

    Room addStudentInRoom(Long studentId, Long roomId);

    List<Student> getStudentsByStatus(StudentStatus status, Long sessionId);

    void editStatusForStudent(Long roomId, EditStudentStatusDto studentStatusDto);

    void leaveRoomForStudent(Long roomId, Long studentId);

    Chat getChatByRoom(Long id);

    void endRoom(Long roomId);

    void openRoom(Long roomId);

    void changeRoomStatus(Long id, RoomStatus newStatus);

    boolean checkIfStudentIsAllowed(Room room, Long studentId);

    void addInterruptionToSession(String time, int totalDuration, Long roomId, Long studentId);

    RoomSummaryDTO getRoomSummary(Long roomId, Long studentId);
}
