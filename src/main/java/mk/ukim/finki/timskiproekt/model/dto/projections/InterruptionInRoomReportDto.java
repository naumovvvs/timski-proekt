package mk.ukim.finki.timskiproekt.model.dto.projections;

import java.time.LocalDateTime;

public interface InterruptionInRoomReportDto {
    String getName();
    String getIndex();
    LocalDateTime getInterruptionTime();
    int getTotalDurationSeconds();
    String getStatus();
}