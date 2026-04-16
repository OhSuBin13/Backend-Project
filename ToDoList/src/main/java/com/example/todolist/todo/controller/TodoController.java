package com.example.todolist.todo.controller;

import com.example.todolist.common.security.AuthenticatedUser;
import com.example.todolist.todo.dto.CreateTodoRequest;
import com.example.todolist.todo.dto.TodoPageResponse;
import com.example.todolist.todo.dto.TodoResponse;
import com.example.todolist.todo.dto.UpdateTodoRequest;
import com.example.todolist.todo.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping("/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodo(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @Valid @RequestBody CreateTodoRequest request
    ) {
        return todoService.create(authenticatedUser.id(), request);
    }

    @GetMapping("/todos")
    public TodoPageResponse getTodos(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return todoService.findAll(authenticatedUser.id(), keyword, pageable);
    }

    @GetMapping("/todos/{todoId}")
    public TodoResponse getTodo(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long todoId
    ) {
        return todoService.findById(authenticatedUser.id(), todoId);
    }

    @PutMapping("/todos/{todoId}")
    public TodoResponse updateTodo(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long todoId,
            @Valid @RequestBody UpdateTodoRequest request
    ) {
        return todoService.update(authenticatedUser.id(), todoId, request);
    }

    @DeleteMapping("/todos/{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
            @PathVariable Long todoId
    ) {
        todoService.delete(authenticatedUser.id(), todoId);
    }
}
