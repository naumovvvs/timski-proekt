package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.model.StudentInRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInRoomRepository extends JpaRepository<StudentInRoom, Long> {
    StudentInRoom findStudentInRoomByRoomAndStudent(Room room, Student student);
}