package api.playercontroller.models.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDeleteResponseDto {
    public Body body;
    public String statusCode;
    public int statusCodeValue;

    static class Body {

    }
}
