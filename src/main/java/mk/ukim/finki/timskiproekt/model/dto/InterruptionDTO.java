package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InterruptionDTO {
    private String time;
    private int totalDuration;
    private Long roomId;
    private Long studentId;
}
