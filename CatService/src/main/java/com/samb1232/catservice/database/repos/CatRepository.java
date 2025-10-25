package com.samb1232.catservice.database.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.samb1232.catservice.database.entities.Cat;

public interface CatRepository extends JpaRepository<Cat, Long> {
    List<Cat> findByUserUserId(Long userId);

    @Query(value = "SELECT * FROM cats ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Cat> findRandomCat();

    @Query(value = "SELECT * FROM cats WHERE cat_id NOT IN :excludedIds ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Cat> findRandomCatExcluding(@Param("excludedIds") List<Long> excludedIds);
}