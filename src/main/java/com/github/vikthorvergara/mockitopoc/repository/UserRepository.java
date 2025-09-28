package com.github.vikthorvergara.mockitopoc.repository;

import com.github.vikthorvergara.mockitopoc.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    User save(User user);

    List<User> findAll();

    List<User> findByEmail(String email);

    void deleteById(Long id);

    boolean existsById(Long id);
}