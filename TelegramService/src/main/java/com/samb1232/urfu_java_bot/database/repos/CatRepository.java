package com.samb1232.urfu_java_bot.database.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samb1232.urfu_java_bot.database.entities.Cat;

public interface CatRepository extends JpaRepository<Cat, Long> {
    List<Cat> findByUserUserId(Long userId);
    
}