package api.playercontroller.models.requestDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerGetByPlayerIdRequestDto {
    public int playerId;
}
