package itmo.info.security.lab.rest.controller;

import org.springframework.web.bind.annotation.RestController;

import itmo.info.security.lab.exception.ForbiddenException;
import itmo.info.security.lab.model.dto.CreatePostRequest;
import itmo.info.security.lab.model.dto.PageInfo;
import itmo.info.security.lab.model.dto.PostDTO;
import itmo.info.security.lab.service.PostService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    final PostService postService;

    @GetMapping("/data")
    public PageInfo<PostDTO> getAllPosts(Pageable pageable) {
        return postService.getAll(pageable);
    }

    @PostMapping("/posts")
    public PostDTO createPost(@RequestBody CreatePostRequest body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth instanceof UsernamePasswordAuthenticationToken) {
            return postService.create(body, auth.getName());
        } else
            throw new ForbiddenException("Not authorized");

    }

}
