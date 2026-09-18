package itmo.info.security.lab.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostDTO {
    private UUID uuid;
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private String postedBy;
}
