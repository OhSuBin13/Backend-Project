package com.example.url_shortening.service;

import com.example.url_shortening.dto.LinkDto;
import com.example.url_shortening.dto.LinkExtraDto;

public interface LinkService {
    LinkDto save(LinkDto dto);

    LinkDto updatedByShortCode(String shortCode, LinkDto dto);

    LinkDto getDtoByShortCode(String shortCode);

    boolean deleteByShortCode(String shortCode);

    LinkExtraDto getExtraDtoByShortCode(String shortCode);
}
