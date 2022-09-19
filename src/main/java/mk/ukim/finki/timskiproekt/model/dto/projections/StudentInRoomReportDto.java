package mk.ukim.finki.timskiproekt.model.dto.projections;

import java.time.LocalDateTime;

public interface StudentInRoomReportDto {
    String getName();
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    String getIndex();
    LocalDateTime getEnterTime();
    LocalDateTime getLeaveTime();
    String getStatus();
}
