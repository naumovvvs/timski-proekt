package mk.ukim.finki.timskiproekt.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Professor extends AppUser {

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    public Professor() {
        super();
    }

    public Professor(String name, String username, String password, String email, LocalDate birthDate, Role role) {
        super(name, username, password, email, birthDate, role);
    }
}