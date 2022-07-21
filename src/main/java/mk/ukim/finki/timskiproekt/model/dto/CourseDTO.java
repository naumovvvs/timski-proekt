package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.Semester;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private String name;
    private String code;
    private String imageUrl;
    private Semester semester;
}
