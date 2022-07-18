package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EditRoomDto {
    private String name;
    private LocalDateTime openFrom;
    private LocalDateTime openTo;
}
