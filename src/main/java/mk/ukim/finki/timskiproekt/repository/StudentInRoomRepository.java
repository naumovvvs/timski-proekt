package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.model.StudentInRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentInRoomRepository extends JpaRepository<StudentInRoom, Long> {
    List<StudentInRoom> findAllByRoomAndStudent(Room room, Student student);
}