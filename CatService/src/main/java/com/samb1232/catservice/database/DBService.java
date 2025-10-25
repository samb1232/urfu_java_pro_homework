package com.samb1232.catservice.database;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samb1232.catservice.database.entities.Cat;
import com.samb1232.catservice.database.entities.CatReaction;
import com.samb1232.catservice.database.entities.User;
import com.samb1232.catservice.database.repos.CatReactionRepository;
import com.samb1232.catservice.database.repos.CatRepository;
import com.samb1232.catservice.database.repos.UserRepository;
import com.samb1232.common.dto.TGUser;

import jakarta.persistence.EntityNotFoundException;


@Service
public class DBService {
    private final CatRepository catRepository;
    private final UserRepository userRepository;
    private final CatReactionRepository catReactionRepository;

    @Autowired
    public DBService(
        CatRepository catRepository, 
        UserRepository userRepository, 
        CatReactionRepository catReactionRepository
        ) {
        this.catRepository = catRepository;
        this.userRepository = userRepository;
        this.catReactionRepository = catReactionRepository;
    }

    @Transactional
    public Cat createCat(Long userId, String photoPath, String catName) {
        User user = getUserById(userId);
        Cat cat = new Cat();
        cat.setUser(user);
        cat.setPhotoPath(photoPath);
        cat.setName(catName != null && !catName.isEmpty() ? catName : "Unnamed Cat");
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
    public User getOrCreateUser(TGUser telegramUser) {
        Long userId = telegramUser.getId();
        
        Optional<User> userOpt = userRepository.findByUserId(userId);

        if (userOpt.isEmpty()) {
            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setName(telegramUser.getFirstName());
            return userRepository.save(newUser);
        }
        return userOpt.get();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public void setReaction(Long userId, Long catId, CatReaction.ReactionType reactionType) {
        User user = getUserById(userId);
        Cat cat = getCatById(catId);
        
        Optional<CatReaction> existingReaction = catReactionRepository.findByUserUserIdAndCatCatId(userId, catId);
        
        if (existingReaction.isPresent()) {
            CatReaction reaction = existingReaction.get();
            if (reaction.getReaction() != reactionType) {
                reaction.setReaction(reactionType);
                catReactionRepository.save(reaction);
            }
        } else {
            CatReaction newReaction = new CatReaction();
            newReaction.setUser(user);
            newReaction.setCat(cat);
            newReaction.setReaction(reactionType);
            try {
                catReactionRepository.save(newReaction);
            } catch (DataIntegrityViolationException e) {
                throw new IllegalStateException("Reaction already exists for this user and cat");
            }
        }
    }               

    public int getLikesCount(Long catId) {
        return catReactionRepository.countLikesByCatId(catId);
    }

    public int getDislikesCount(Long catId) {
        return catReactionRepository.countDislikesByCatId(catId);
    }

    public Optional<CatReaction.ReactionType> getUserReaction(Long userId, Long catId) {
        return catReactionRepository.findByUserUserIdAndCatCatId(userId, catId)
                .map(CatReaction::getReaction);
    }
    
    @Transactional
    public List<Cat> getCatsWithReactionsByUser(Long userId) {
        return catRepository.findByUserUserId(userId);
    }

    public Optional<Cat> getRandomCat() {
        return catRepository.findRandomCat();
    }
}