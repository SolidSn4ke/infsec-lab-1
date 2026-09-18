package itmo.info.security.lab.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import itmo.info.security.lab.exception.NotFoundException;
import itmo.info.security.lab.model.dto.PageInfo;
import itmo.info.security.lab.model.dto.PostDTO;
import itmo.info.security.lab.model.dto.request.CreatePostRequest;
import itmo.info.security.lab.model.entity.Post;
import itmo.info.security.lab.model.entity.User;
import itmo.info.security.lab.model.repository.PostRepository;
import itmo.info.security.lab.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
    final PostRepository postRepo;

    final UserRepository userRepo;

    final ModelMapper mapper;

    public PageInfo<PostDTO> getAll(Pageable pageable) {
        Page<Post> data = postRepo.findAll(pageable);
        return PageInfo.<PostDTO>builder()
                .items(data.getContent().stream().map(e -> mapper.map(e, PostDTO.class)).toList())
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPages(data.getTotalPages()).totalElements(data.getTotalElements()).build();
    }

    public PostDTO create(CreatePostRequest body, String poster) {
        User user = userRepo.findById(poster).orElseThrow(() -> new NotFoundException("User not found"));

        validateCreatePostRequest(body);

        Post post = new Post();
        post.setTitle(body.getTitle());
        post.setBody(body.getBody());
        post.setPostedBy(user);

        post = postRepo.save(post);

        return mapper.map(post, PostDTO.class);
    }

    private void validateCreatePostRequest(CreatePostRequest request) {
        if (request.getTitle() == null || request.getTitle().length() > 128) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid title");
        }
        if (request.getBody() != null && request.getBody().length() > 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid body");
        }
    }
}
