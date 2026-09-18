package itmo.info.security.lab.model.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import itmo.info.security.lab.model.entity.Post;

public interface PostRepository extends JpaRepository<Post, UUID> {

}
