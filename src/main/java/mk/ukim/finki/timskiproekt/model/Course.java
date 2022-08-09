package mk.ukim.finki.timskiproekt.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mk.ukim.finki.timskiproekt.model.enums.Semester;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

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

    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Room> rooms = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();

//    public Course(Long id, String name, String code, String imageUrl, Semester semester, List<Room> rooms) {
//        this.id = id;
//        this.name = name;
//        this.code = code;
//        this.imageUrl = imageUrl;
//        this.semester = semester;
//        this.rooms = rooms;
//    }
}