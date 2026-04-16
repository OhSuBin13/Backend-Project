package com.example.todolist.common.security;

public record AuthenticatedUser(
        Long id,
        String email,
        String name
) {
}
