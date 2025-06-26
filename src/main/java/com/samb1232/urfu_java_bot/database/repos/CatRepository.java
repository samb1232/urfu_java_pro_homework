package com.samb1232.urfu_java_bot.database.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.samb1232.urfu_java_bot.database.entities.Cat;

public interface CatRepository extends JpaRepository<Cat, Long> {
    List<Cat> findByUserUserId(Long userId);
    
    @Modifying
    @Query("UPDATE Cat c SET c.likesCount = c.likesCount + 1 WHERE c.catId = :catId")
    void incrementLikes(@Param("catId") Long catId);

    @Modifying
    @Query("UPDATE Cat c SET c.dislikesCount = c.dislikesCount + 1 WHERE c.catId = :catId")
    void incrementDislikes(@Param("catId") Long catId);

    
}