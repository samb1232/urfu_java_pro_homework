package com.samb1232.catservice.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "viewed_cats")
@IdClass(ViewedCat.ViewedCatId.class)
public class ViewedCat {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    public ViewedCat() {
        this.viewedAt = LocalDateTime.now();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Cat getCat() {
        return cat;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }

    public static class ViewedCatId implements Serializable {
        private Long user;
        private Long cat;

        public ViewedCatId() {}

        public ViewedCatId(Long user, Long cat) {
            this.user = user;
            this.cat = cat;
        }

        public Long getUser() {
            return user;
        }

        public void setUser(Long user) {
            this.user = user;
        }

        public Long getCat() {
            return cat;
        }

        public void setCat(Long cat) {
            this.cat = cat;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ViewedCatId)) return false;
            ViewedCatId that = (ViewedCatId) o;
            return user.equals(that.user) && cat.equals(that.cat);
        }

        @Override
        public int hashCode() {
            return 31 * user.hashCode() + cat.hashCode();
        }
    }
}
