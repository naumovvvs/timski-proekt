package mk.ukim.finki.timskiproekt.repository;

import mk.ukim.finki.timskiproekt.model.Chat;
import mk.ukim.finki.timskiproekt.model.dto.projections.MessageInRoomReportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(value =
            "select r.name, s.index, m.content, m.sent_at as sentAt\n" +
                    "from room r, chat c, message m, student s\n" +
                    "where r.id = :roomId and r.chat_id = c.id and c.id = m.chat_id and s.id = m.sender_id",
            nativeQuery = true)
    List<MessageInRoomReportDto> generateMessageInRoomReport(@Param("roomId") Long roomId);
}