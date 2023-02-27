package api.playercontroller.models.responseDto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerGetAllResponseDto {
    public List<PlayerItem> players;

}


