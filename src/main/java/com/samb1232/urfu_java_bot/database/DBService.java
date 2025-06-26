package com.samb1232.urfu_java_bot.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samb1232.urfu_java_bot.database.entities.Cat;
import com.samb1232.urfu_java_bot.database.entities.User;
import com.samb1232.urfu_java_bot.database.repos.CatRepository;
import com.samb1232.urfu_java_bot.database.repos.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DBService {
    private final CatRepository catRepository;
    private final UserRepository userRepository;

    @Autowired
    public DBService(CatRepository catRepository, UserRepository userRepository) {
        this.catRepository = catRepository;
        this.userRepository = userRepository;
    }

    public Cat createCat(Long userId, String photoPath) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        
        Cat cat = new Cat();
        cat.setUser(user);
        cat.setPhotoPath(photoPath);
        return catRepository.save(cat);
    }

    public List<Cat> getCatsByUser(Long userId) {
        return catRepository.findByUserUserId(userId);
    }

    public Cat getCatById(Long catId) {
        return catRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Cat not found"));
    }

    @Transactional
    public void deleteCat(Long catId) {
        catRepository.deleteById(catId);
    }

    @Transactional
    public void likeCat(Long catId) {
        catRepository.incrementLikes(catId);
    }

    @Transactional
    public void dislikeCat(Long catId) {
        catRepository.incrementDislikes(catId);
    }
}