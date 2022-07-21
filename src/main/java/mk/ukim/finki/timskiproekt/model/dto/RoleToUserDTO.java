package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleToUserDTO {
    private String username;
    private String rollName;
}
