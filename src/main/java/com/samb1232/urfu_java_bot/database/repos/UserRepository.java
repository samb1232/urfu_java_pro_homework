package com.samb1232.urfu_java_bot.database.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samb1232.urfu_java_bot.database.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}