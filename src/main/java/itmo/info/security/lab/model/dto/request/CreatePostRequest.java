package itmo.info.security.lab.model.dto.request;

import org.springframework.web.util.HtmlUtils;

import lombok.Getter;

@Getter
public class CreatePostRequest {
    private String title;
    private String body;

    public void setTitle(String title) {
        this.title = title == null ? null : HtmlUtils.htmlEscape(title);
    }

    public void setBody(String body) {
        this.body = body == null ? null : HtmlUtils.htmlEscape(body);
    }
}
