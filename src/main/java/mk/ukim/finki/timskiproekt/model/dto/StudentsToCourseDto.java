package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StudentsToCourseDto {
    private String courseCode;
    private List<String> studentIndexes;
}
