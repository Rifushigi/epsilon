package com.rifushigi.epsilon.dao;

import com.rifushigi.epsilon.entity.ShortUrl;
import com.rifushigi.epsilon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShortUrlRepository extends JpaRepository<ShortUrl, UUID> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    @Query("SELECT s FROM ShortUrl s WHERE s.shortCode = :shortCode AND s.expiresAt > :now")
    Optional<ShortUrl> findByShortCodeAndNotExpired(@Param("shortCode") String shortCode,
                                                    @Param("now") LocalDateTime now);

    Page<ShortUrl> findByUserOrderByClickCountDesc(@Param("user")User user, Pageable pageable);

    @Query("SELECT s FROM ShortUrl s WHERE s.user = :user AND s.expiresAt > :now ORDER BY s.createdAt DESC")
    Page<ShortUrl> findByUserAndNotExpiredOrderByCreatedAtDesc(@Param("user") User user,
                                                               @Param("now") LocalDateTime now,
                                                               Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ShortUrl s SET s.clickCount = s.clickCount + 1 WHERE s.id = :id")
    void incrementClickCount(@Param("id") UUID id);

    boolean existsByShortCode(String shortCode);

    @Query("SELECT COUNT(s) FROM ShortUrl s WHERE s.user = :user")
    long countByUser(@Param("user") User user);
}
