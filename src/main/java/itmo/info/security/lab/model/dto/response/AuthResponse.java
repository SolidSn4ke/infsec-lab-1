package itmo.info.security.lab.model.dto.response;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String jwt;
    private String tokenType;
    private Date expirationDate;
}
