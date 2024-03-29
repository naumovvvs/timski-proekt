package mk.ukim.finki.timskiproekt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    public Professor() {
        super();
    }

    public Professor(String name, String username, String password, String email, LocalDate birthDate) {
        super(name, username, password, email, birthDate);
    }
}