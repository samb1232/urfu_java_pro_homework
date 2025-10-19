package com.samb1232.urfu_java_bot.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.samb1232.urfu_java_bot.dto.CatInfo;

@Service
public class CatCacheService {
    private final Map<Long, List<CatInfo>> userCatsCache = new HashMap<>();
    private final Map<Long, CatInfo> catDetailsCache = new HashMap<>();

    public void storeCatsForUser(Long userId, List<CatInfo> cats) {
        storeUserCats(userId, cats);
        storeCatDetails(cats);
    }

    private void storeUserCats(Long userId, List<CatInfo> cats) {
        userCatsCache.put(userId, cats);
    }

    private void storeCatDetails(List<CatInfo> cats) {
        for (CatInfo cat : cats) {
            catDetailsCache.put(cat.getId(), cat);
        }
    }

    public List<CatInfo> getCatsForUser(Long userId) {
        return userCatsCache.get(userId);
    }

    public CatInfo getCatById(Long catId) {
        return catDetailsCache.get(catId);
    }

    public void clearUserCache(Long userId) {
        List<CatInfo> cats = userCatsCache.remove(userId);
        if (cats != null) {
            for (CatInfo cat : cats) {
                catDetailsCache.remove(cat.getId());
            }
        }
    }
}
