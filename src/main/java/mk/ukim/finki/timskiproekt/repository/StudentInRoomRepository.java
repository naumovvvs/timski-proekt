package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.model.StudentInRoom;
import mk.ukim.finki.timskiproekt.model.dto.projections.StudentInRoomReportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentInRoomRepository extends JpaRepository<StudentInRoom, Long> {
    List<StudentInRoom> findAllByRoomAndStudent(Room room, Student student);

    @Query(value =
            "SELECT r.name, r.start_time as startTime, r.end_time as endTime, s.index, " +
                    "sr.enter_time as enterTime, sr.leave_time as leaveTime, sr.status\n" +
            "from room as r, student_in_room as sr, student as s\n" +
            "where r.id = :roomId and s.id = sr.student_id and sr.room_id = r.id",
            nativeQuery = true)
    List<StudentInRoomReportDto> generateStudentInRoomReport(@Param("roomId") Long roomId);
}