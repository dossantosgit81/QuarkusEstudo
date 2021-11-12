package io.github.mendes.socialstudy.domain.repository;

import javax.enterprise.context.ApplicationScoped;

import io.github.mendes.socialstudy.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User>{

}
