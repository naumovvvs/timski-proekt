package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class SaveRoomDto {
    private String name;
    private LocalDateTime openFrom;
    private LocalDateTime openTo;
    private Long courseId;
    private Long moderatorId;
    private List<String> allowedStudents;
}
