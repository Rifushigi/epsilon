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

    @Query("select s from ShortUrl s where s.shortCode = :shortCode and s.expiresAt > :now")
    Optional<ShortUrl> findByShortCodeAndNotExpired(@Param("shortCode") String shortCode,
                                                    @Param("now") LocalDateTime now);

    Page<ShortUrl> findByUserOrderByClickCountDesc(@Param("user")User user, Pageable pageable);

    @Query("select s from ShortUrl s where s.user = :user and s.expiresAt > :now order by s.createdAt desc")
    Page<ShortUrl> findByUserAndNotExpiredOrderByCreatedAtDesc(@Param("user") User user,
                                                               @Param("now") LocalDateTime now,
                                                               Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update ShortUrl s set s.clickCount = s.clickCount + 1 where s.id = :id")
    void incrementClickCount(@Param("id") UUID id);

    boolean existsByShortCode(String shortCode);

    @Query("select COUNT(s) from ShortUrl s where s.user = :user")
    long countByUser(@Param("user") User user);
}
