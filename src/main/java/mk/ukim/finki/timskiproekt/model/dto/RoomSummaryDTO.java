package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomSummaryDTO {
    private String name;
    private Long roomDuration;
    private String studentFullName;
    private int totalInterruptions;
    private int interruptionsDuration;
    private String studentStatus;
}
