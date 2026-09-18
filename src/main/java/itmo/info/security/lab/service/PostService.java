package itmo.info.security.lab.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import itmo.info.security.lab.exception.NotFoundException;
import itmo.info.security.lab.model.dto.CreatePostRequest;
import itmo.info.security.lab.model.dto.PageInfo;
import itmo.info.security.lab.model.dto.PostDTO;
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

        Post post = new Post();
        post.setTitle(body.getTitle());
        post.setBody(body.getBody());
        post.setPostedBy(user);

        post = postRepo.save(post);

        return mapper.map(post, PostDTO.class);
    }
}
