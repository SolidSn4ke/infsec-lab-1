package itmo.info.security.lab.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PageInfo<T> {
    private List<T> items;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
}
