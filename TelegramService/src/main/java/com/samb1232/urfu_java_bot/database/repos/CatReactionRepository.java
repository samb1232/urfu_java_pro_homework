package com.samb1232.urfu_java_bot.database.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.samb1232.urfu_java_bot.database.entities.CatReaction;

public interface CatReactionRepository extends JpaRepository<CatReaction, Long> {
    Optional<CatReaction> findByUserUserIdAndCatCatId(Long userId, Long catId);
    
    boolean existsByUserUserIdAndCatCatId(Long userId, Long catId);
    
    @Query("SELECT COUNT(cr) FROM CatReaction cr WHERE cr.cat.catId = :catId AND cr.reaction = 'LIKE'")
    long countLikesByCatId(@Param("catId") Long catId);
    
    @Query("SELECT COUNT(cr) FROM CatReaction cr WHERE cr.cat.catId = :catId AND cr.reaction = 'DISLIKE'")
    long countDislikesByCatId(@Param("catId") Long catId);
}