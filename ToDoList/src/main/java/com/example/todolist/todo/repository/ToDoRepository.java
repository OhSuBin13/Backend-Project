package com.example.todolist.todo.repository;

import com.example.todolist.todo.entity.Todo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoRepository extends JpaRepository<Todo, Long> {

    Page<Todo> findByUserId(Long userId, Pageable pageable);

    Page<Todo> findByUserIdAndTitleContainingIgnoreCase(Long userId, String keyword, Pageable pageable);

    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
