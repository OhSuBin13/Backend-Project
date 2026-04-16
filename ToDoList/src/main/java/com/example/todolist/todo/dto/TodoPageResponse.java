package com.example.todolist.todo.dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record TodoPageResponse(
        List<TodoResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) {
    public static TodoPageResponse from(Page<TodoResponse> page) {
        return new TodoPageResponse(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
