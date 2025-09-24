package com.rifushigi.epsilon.dao;

import com.rifushigi.epsilon.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.username = :usernameOrEmail or u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail( @Param("usernameOrEmail") String usernameOrEmail);

    @Query("select (count(u) > 0) from User u where u.email = :usernameOrEmail or u.username = :usernameOrEmail")
    boolean existsByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
