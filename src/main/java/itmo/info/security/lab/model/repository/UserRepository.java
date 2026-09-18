package itmo.info.security.lab.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import itmo.info.security.lab.model.entity.User;

public interface UserRepository extends JpaRepository<User, String> {

}
