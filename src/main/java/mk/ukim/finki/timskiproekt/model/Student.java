package mk.ukim.finki.timskiproekt.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    private List<Course> courses = new ArrayList<>();

    public Student() {
        super();
    }

    public Student(String name, String username, String password, String email, LocalDate birthDate) {
        super(name, username, password, email, birthDate);
        this.index = generateIndex();
    }

    private Long generateIndex() {
        int yearStudies = LocalDate.now().getYear();
        int year = yearStudies%100;
        int random = getUsername().hashCode()%10000;
        return Integer.toUnsignedLong((year*10000)+random);
    }
}