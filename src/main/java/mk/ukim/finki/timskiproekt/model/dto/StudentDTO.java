package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private Long id;
    private String name;
    private String username;
    private String email;
    private LocalDate birthDate;
    private List<RoleDTO> roles;
    private Long index;
    private List<CourseDTO> courses;
}
