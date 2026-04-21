package com.example.url_shortening.repository;

import com.example.url_shortening.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByShortCode(String shortCode);

    Optional<Link> findByUrl(String url);

    boolean existsByShortCode(String shortCode);
}
