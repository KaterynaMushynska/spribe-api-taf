package api.playercontroller.models.requestDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerUpdateRequestDto {
    public int age;
    public String gender;
    public String login;
    public String password;
    public String role;
    public String screenName;
}
