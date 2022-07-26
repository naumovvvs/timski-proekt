package mk.ukim.finki.timskiproekt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgoraTokenDTO {
    private Long roomId;
    private Long userId;
}
