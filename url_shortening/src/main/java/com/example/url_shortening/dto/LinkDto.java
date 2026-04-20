package com.example.url_shortening.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LinkDto(
        Long id,
        String url,
        String shortCode,
        LocalDateTime createdDate,
        LocalDateTime updatedDate
) {
}
