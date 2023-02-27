package api.playercontroller.models.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerUpdateResponseDto {
    public int age;
    public String gender;
    public int id;
    public String login;
    public String role;
    public String screenName;
}
