package com.example.todolist.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTodoRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be 100 characters or fewer")
        String title,

        @Size(max = 1000, message = "Description must be 1000 characters or fewer")
        String description
) {
}
