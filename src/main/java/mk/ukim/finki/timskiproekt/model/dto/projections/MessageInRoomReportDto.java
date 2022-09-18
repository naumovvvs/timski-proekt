package mk.ukim.finki.timskiproekt.model.dto.projections;

import java.time.LocalDateTime;

public interface MessageInRoomReportDto {
    String getName();
    String getContent();
    LocalDateTime getSentAt();
    String getIndex();
}
