package mk.ukim.finki.timskiproekt.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Interruption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime interruptionTime;
    private int totalDurationSeconds;

    public Interruption(LocalDateTime interruptionTime, int totalDurationSeconds) {
        this.interruptionTime = interruptionTime;
        this.totalDurationSeconds = totalDurationSeconds;
    }
}
