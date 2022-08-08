package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseToUserDTO {
    private String courseName;
    private Long userId;
}
