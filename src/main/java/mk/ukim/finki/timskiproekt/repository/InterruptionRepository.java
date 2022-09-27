package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Interruption;
import mk.ukim.finki.timskiproekt.model.dto.projections.InterruptionInRoomReportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterruptionRepository extends JpaRepository<Interruption, Long> {
    @Query(value =
            "SELECT r.name, s.index, i.interruption_time as interruptionTime, i.total_duration_seconds as totalDurationSeconds, sr.status\n" +
                    "from room as r, student_in_room as sr, student_in_room_interruptions as sri, interruption as i, student as s\n" +
                    "where r.id = :roomId and r.id = sr.room_id and sr.id = sri.student_in_room_id and " +
                    "i.id = sri.interruptions_id and s.id = sr.student_id",
            nativeQuery = true)
    List<InterruptionInRoomReportDto> generateInterruptionsInRoomReport(@Param("roomId") Long roomId);
}
