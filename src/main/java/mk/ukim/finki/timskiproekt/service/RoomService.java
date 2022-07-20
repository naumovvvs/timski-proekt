package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.EditRoomDto;
import mk.ukim.finki.timskiproekt.model.dto.SaveRoomDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface RoomService {
    Room getRoomById(Long id);

    Room getRoomByName(String name);

    List<Room> getAllRoomsByCourse(Long id);

    Session getSessionByRoom(Long id);

    Professor getModeratorByRoom(Long id);

    Map<LocalDateTime, LocalDateTime> getRoomTimeSlot(Long id);

    List<Student> getAllowedStudentsByRoom(Long id);

    Room create(SaveRoomDto roomDto);

    Room update(Long id, EditRoomDto roomDto);

    void delete(Long id);
}
