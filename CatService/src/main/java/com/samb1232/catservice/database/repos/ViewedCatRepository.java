package com.samb1232.catservice.database.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.samb1232.catservice.database.entities.ViewedCat;

public interface ViewedCatRepository extends JpaRepository<ViewedCat, ViewedCat.ViewedCatId> {

    @Query("SELECT vc.cat.catId FROM ViewedCat vc WHERE vc.user.userId = :userId")
    List<Long> findViewedCatIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ViewedCat vc WHERE vc.user.userId = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
