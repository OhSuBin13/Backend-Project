package com.example.blog_api.repository;

import com.example.blog_api.model.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("SELECT b FROM Blog b WHERE b.category = %:keyword% OR b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    List<Blog> findByKeyword(@Param("keyword") String keyword);
}
