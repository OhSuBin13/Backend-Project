package com.example.url_shortening.service;

import com.example.url_shortening.dto.LinkDto;
import com.example.url_shortening.dto.LinkExtraDto;

public interface LinkService {
    LinkDto create(LinkDto dto);

    LinkDto updateByShortCode(String shortCode, LinkDto dto);

    LinkDto getByShortCode(String shortCode);

    LinkExtraDto getExtraByShortCode(String shortCode);

    void incrementAccessCount(String shortCode);

    void deleteByShortCode(String shortCode);
}
