package mk.ukim.finki.timskiproekt.service.impl;

import lombok.extern.slf4j.Slf4j;
import mk.ukim.finki.timskiproekt.model.*;
import mk.ukim.finki.timskiproekt.model.dto.EditRoomDto;
import mk.ukim.finki.timskiproekt.model.dto.SaveRoomDto;
import mk.ukim.finki.timskiproekt.repository.CourseRepository;
import mk.ukim.finki.timskiproekt.repository.ProfessorRepository;
import mk.ukim.finki.timskiproekt.repository.RoomRepository;
import mk.ukim.finki.timskiproekt.service.RoomService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final CourseRepository courseRepository;
    private final ProfessorRepository professorRepository;

    public RoomServiceImpl(RoomRepository roomRepository,
                           CourseRepository courseRepository,
                           ProfessorRepository professorRepository) {
        this.roomRepository = roomRepository;
        this.courseRepository = courseRepository;
        this.professorRepository = professorRepository;
    }

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
    public List<Room> getAllRoomsByCourse(Long id) {
        Course course = this.courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Course with id: %d not found!", id)));
        log.info("Getting all rooms by course id: {}", id);
        return this.roomRepository.findAllByCourse(course);
    }

    @Override
    public Session getSessionByRoom(Long id) {
        log.info("Getting session by room id: {}", id);
        return this.getRoomById(id).getSession();
    }

    @Override
    public Professor getModeratorByRoom(Long id) {
        log.info("Getting moderator by room id: {}", id);
        return this.getRoomById(id).getModerator();
    }

    @Override
    public Map<LocalDateTime, LocalDateTime> getRoomTimeSlot(Long id) {
        Room room = this.getRoomById(id);
        HashMap<LocalDateTime, LocalDateTime> timeSlot = new HashMap<>();
        timeSlot.put(room.getOpenFrom(), room.getOpenTo());
        log.info("Getting time slot by room id: {}", id);
        return timeSlot;
    }

    @Override
    public List<Student> getAllowedStudentsByRoom(Long id) {
        log.info("Getting allowed students by room id: {}", id);
        return this.getRoomById(id).getAllowedStudents();
    }

    @Override
    public Room create(SaveRoomDto roomDto) {
        Course course = this.courseRepository.findById(roomDto.getCourseId())
                .orElseThrow(() -> new RuntimeException(String.format("Course with id: %d not found!", roomDto.getCourseId())));
        Professor moderator = (Professor) this.professorRepository.findById(roomDto.getModeratorId())
                .orElseThrow(() -> new RuntimeException(String.format("Moderator with id: %d not found!", roomDto.getModeratorId())));

        Room room = new Room(roomDto.getName(), roomDto.getOpenFrom(), roomDto.getOpenTo(), course, moderator);
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
}
