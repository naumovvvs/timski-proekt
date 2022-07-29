package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Room findByName(String name);

    List<Room> findAllByCourse(Course course);

    Room findBySession(Session session);
}