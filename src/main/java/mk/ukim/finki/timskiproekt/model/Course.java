package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.Semester;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;

    @Enumerated(value = EnumType.STRING)
    private Semester semester;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private Collection<Room> rooms = new ArrayList<>();
}