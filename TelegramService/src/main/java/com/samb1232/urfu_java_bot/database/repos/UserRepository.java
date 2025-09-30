package com.samb1232.urfu_java_bot.database.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samb1232.urfu_java_bot.database.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(Long userId);
}