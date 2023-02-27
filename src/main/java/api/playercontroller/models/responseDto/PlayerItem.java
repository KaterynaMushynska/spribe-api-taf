package api.playercontroller.models.responseDto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerItem {
    public int age;
    public String gender;
    public int id;
    public String role;
    public String screenName;
}
