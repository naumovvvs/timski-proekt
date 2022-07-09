package mk.ukim.finki.timskiproekt.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import java.time.LocalDate;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Admin extends AppUser{
    public Admin() {
        super();
    }

    public Admin(String name, String username, String password, String email, LocalDate birthDate, Role role) {
        super(name, username, password, email, birthDate, role);
    }
}
