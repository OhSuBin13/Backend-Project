package com.example.blog_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "title", length = 100, unique = false)
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Column(nullable = false, name = "content", length = 10000, unique = false)
    @Size(min = 5, max = 1000, message = "content must be between 100 and 10000 characters")
    @NotBlank(message = "content is mandatory")
    private String content;

    @Column(nullable = false, name = "category", length = 70, unique = false)
    private String category;

    @ElementCollection
    @BatchSize(size = 20)
    private List<String> tags;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
