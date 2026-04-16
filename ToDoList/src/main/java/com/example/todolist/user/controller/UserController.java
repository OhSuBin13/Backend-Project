package com.example.todolist.user.controller;

import com.example.todolist.common.security.AuthenticatedUser;
import com.example.todolist.user.dto.CurrentUserResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/me")
    public CurrentUserResponse getCurrentUser(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        return new CurrentUserResponse(
                authenticatedUser.id(),
                authenticatedUser.name(),
                authenticatedUser.email()
        );
    }
}
