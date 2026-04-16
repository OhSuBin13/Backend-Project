package com.example.todolist.todo.service;

import com.example.todolist.todo.dto.CreateTodoRequest;
import com.example.todolist.todo.dto.TodoPageResponse;
import com.example.todolist.todo.dto.TodoResponse;
import com.example.todolist.todo.dto.UpdateTodoRequest;
import com.example.todolist.todo.entity.Todo;
import com.example.todolist.todo.repository.ToDoRepository;
import com.example.todolist.user.entity.User;
import com.example.todolist.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class TodoService {

    private final ToDoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoService(ToDoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TodoResponse create(Long userId, CreateTodoRequest request) {
        User user = findUser(userId);
        Todo todo = new Todo(request.title(), request.description(), user);
        Todo savedTodo = todoRepository.save(todo);
        return TodoResponse.from(savedTodo);
    }

    public TodoPageResponse findAll(Long userId, String keyword, Pageable pageable) {
        Page<TodoResponse> page = hasKeyword(keyword)
                ? todoRepository.findByUserIdAndTitleContainingIgnoreCase(userId, keyword.trim(), pageable)
                    .map(TodoResponse::from)
                : todoRepository.findByUserId(userId, pageable)
                    .map(TodoResponse::from);

        return TodoPageResponse.from(page);
    }

    public TodoResponse findById(Long userId, Long todoId) {
        return TodoResponse.from(findTodo(userId, todoId));
    }

    @Transactional
    public TodoResponse update(Long userId, Long todoId, UpdateTodoRequest request) {
        Todo todo = findTodo(userId, todoId);
        todo.update(request.title(), request.description());
        return TodoResponse.from(todo);
    }

    @Transactional
    public void delete(Long userId, Long todoId) {
        Todo todo = findTodo(userId, todoId);
        todoRepository.delete(todo);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private Todo findTodo(Long userId, Long todoId) {
        return todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));
    }

    private boolean hasKeyword(String keyword) {
        return keyword != null && !keyword.trim().isEmpty();
    }
}
