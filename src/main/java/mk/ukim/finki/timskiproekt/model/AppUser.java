package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique=true)
    private String username;
    private String password;
    private String email;
    private LocalDate birthDate;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<Role> roles = new ArrayList<>();

    public AppUser(String name, String username, String password, String email, LocalDate birthDate) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
    }
}
