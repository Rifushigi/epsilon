package com.rifushigi.epsilon.dao;

import com.rifushigi.epsilon.entity.Click;
import com.rifushigi.epsilon.entity.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ClickRepository extends JpaRepository<Click, UUID> {

    Page<Click> findByShortUrlOrderByClickedAtDesc(ShortUrl shortUrl, Pageable pageable);

    @Query("select c from Click c where c.shortUrl = :shortUrl")
    Page<Click> findLatestClicksByShortUrl(@Param("shortUrl") ShortUrl shortUrl, Pageable pageable);
}
