package itmo.info.security.lab.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    private String login;
    private String password;
}
