package io.github.mendes.socialstudy.domain.repository;

import javax.enterprise.context.ApplicationScoped;

import io.github.mendes.socialstudy.domain.model.Post;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class PostRepository implements PanacheRepository<Post>{

}
