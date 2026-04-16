package com.example.todolist.user.dto;

public record CurrentUserResponse(
        Long id,
        String name,
        String email
) {
}
