package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.Semester;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique=true)
    private String code;

    private String imageUrl;

    @Enumerated(value = EnumType.STRING)
    private Semester semester;

    @OneToMany(mappedBy = "course", fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();

}