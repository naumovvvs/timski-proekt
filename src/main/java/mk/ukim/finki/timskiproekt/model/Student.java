package mk.ukim.finki.timskiproekt.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Student extends AppUser {
    @Column(unique=true)
    private Long index;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Course> courses = new ArrayList<>();

    public Student() {
        super();
    }

    public Student(String name, String username, String password, String email, LocalDate birthDate, Long index, Role role) {
        super(name, username, password, email, birthDate, role);
        this.index = index;
    }
}