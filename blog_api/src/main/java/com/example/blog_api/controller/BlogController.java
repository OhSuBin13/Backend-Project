package com.example.blog_api.controller;

import com.example.blog_api.model.Blog;
import com.example.blog_api.services.BlogServices;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class BlogController {

    private final BlogServices blogServices;

    public BlogController(BlogServices blogServices) {
        this.blogServices = blogServices;
    }

    @PostMapping
    public Blog createBlog(@RequestBody Blog blog) {
        return blogServices.createBlog(blog);
    }

    @PutMapping("/{id}")
    public Blog updateBlog(@PathVariable Long id, @RequestBody Blog blog) {
        return blogServices.updateBlog(id, blog);
    }

    @DeleteMapping("/{id}")
    public boolean deleteBlog(@PathVariable Long id) {
        return blogServices.deleteBlog(id);
    }

    @GetMapping
    public List<Blog> getAllBlogs() {
        return blogServices.getAllBlogs();
    }

    @GetMapping("/search")
    public List<Blog> getBlogsByKeyword(@RequestParam("term") String term) {
        return blogServices.getBlogsByKeywords(term);
    }
}
