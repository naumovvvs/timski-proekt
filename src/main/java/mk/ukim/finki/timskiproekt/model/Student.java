package mk.ukim.finki.timskiproekt.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends AppUser {

    private Long index;

    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<Course> courses = new ArrayList<>();

    public Student() {
        super();
    }

    public Student(String name, String username, String password, String email, LocalDate birthDate, Long index) {
        super(name, username, password, email, birthDate, "ROLE_STUDENT");
        this.index = index;
    }
}