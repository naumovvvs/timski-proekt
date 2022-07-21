package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveUserDTO {
    private String name;
    private String username;
    private String password;
    private String email;
    private LocalDate birthDate;
    private RoleDTO accountType;
    private List<RoleDTO> roles;
}
