package itmo.info.security.lab.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import itmo.info.security.lab.model.dto.PostDTO;
import itmo.info.security.lab.model.entity.Post;

@Configuration
public class AppConfig {

    @Bean
    ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        PropertyMap<Post, PostDTO> postMapping = new PropertyMap<Post, PostDTO>() {
            @Override
            protected void configure() {
                map(source.getPostedBy().getLogin(), destination.getPostedBy());
            }
        };

        mapper.addMappings(postMapping);
        return mapper;
    }

    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
