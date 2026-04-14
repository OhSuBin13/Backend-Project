package com.example.blog_api.services;

import com.example.blog_api.model.Blog;
import com.example.blog_api.repository.BlogRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlogServices {
    private final BlogRepository blogRepo;

    public BlogServices(BlogRepository blogRepo) {
        this.blogRepo = blogRepo;
    }

    @Cacheable(value = "blogs")
    public List<Blog> getAllBlogs() {
        return blogRepo.findAll();
    }

    @Cacheable(value = "blog", key = "#id")
    public Blog getBlogById(Long id) {
        return blogRepo.findById(id).orElse(null);
    }

    @Cacheable(value = "blogs", key = "#keyword")
    public List<Blog> getBlogsByKeywords(String keyword) {
        return blogRepo.findByKeyword(keyword);
    }

    @CacheEvict(value = {"blogs", "blog"}, allEntries = true)
    @Transactional
    public Blog createBlog(Blog blog) {
        return blogRepo.save(blog);
    }

    @CacheEvict(value = {"blogs", "blog"}, allEntries = true)
    @Transactional
    public Blog updateBlog(Long id, Blog blog) {
        Blog existingBlog = blogRepo.findById(id).orElse(null);
        if (existingBlog != null) {
            existingBlog.setTitle(blog.getTitle());
            existingBlog.setContent(blog.getContent());
            existingBlog.setCategory(blog.getCategory());
            existingBlog.setTags(blog.getTags());
            return blogRepo.save(existingBlog);
        }
        return null;
    }

    @CacheEvict(value = {"blogs", "blog"}, allEntries = true)
    @Transactional
    public Boolean deleteBlog(Long id) {
        Blog existingBlog = blogRepo.findById(id).orElse(null);
        if (existingBlog != null) {
            blogRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
