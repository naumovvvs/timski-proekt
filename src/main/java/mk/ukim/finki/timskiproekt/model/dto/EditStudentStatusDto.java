package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditStudentStatusDto {
    private Long studentId;
    private String newStudentStatus;
}
