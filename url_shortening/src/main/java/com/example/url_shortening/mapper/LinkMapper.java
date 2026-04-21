package com.example.url_shortening.mapper;

import com.example.url_shortening.dto.LinkDto;
import com.example.url_shortening.dto.LinkExtraDto;
import com.example.url_shortening.entity.Link;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LinkMapper {

    public LinkDto entityToDto(Link link) {
        Objects.requireNonNull(link, "link must not be null");
        return LinkDto.builder()
                .id(link.getId())
                .shortCode(link.getShortCode())
                .url(link.getUrl())
                .createdDate(link.getCreatedDate())
                .updatedDate(link.getUpdatedDate())
                .build();
    }

    public Link createEntity(LinkDto dto) {
        Objects.requireNonNull(dto, "dto must not be null");
        return Link.create(dto.url());
    }

    public LinkExtraDto entityToExtraDto(Link link) {
        Objects.requireNonNull(link, "link must not be null");
        return LinkExtraDto.builder()
                .id(link.getId())
                .shortCode(link.getShortCode())
                .url(link.getUrl())
                .createdDate(link.getCreatedDate())
                .updatedDate(link.getUpdatedDate())
                .accessCount(link.getAccessCount())
                .build();
    }
}
