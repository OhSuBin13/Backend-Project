package com.example.url_shortening.service;

import com.example.url_shortening.dto.LinkDto;
import com.example.url_shortening.dto.LinkExtraDto;
import com.example.url_shortening.entity.Link;
import com.example.url_shortening.mapper.LinkMapper;
import com.example.url_shortening.repository.LinkRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    @Autowired
    public LinkServiceImpl(LinkRepository linkRepository,
                           LinkMapper linkMapper) {
        this.linkRepository = linkRepository;
        this.linkMapper = linkMapper;
    }

    private String generateKey() {
        int length = 5;
        boolean useLetters = true;
        boolean useNumbers = true;
        String key;

        Optional<Link> optional;
        do {
            key = RandomStringUtils.random(length, useLetters, useNumbers);
            optional = this.linkRepository.getLinkByShortCode(key);
        } while (optional.isPresent());
        return key;
    }

    private void increaseAccessCount(Link link) {
        Objects.requireNonNull(link.getId());
        linkRepository.increaseAccessCount(link.getId());
    }

    private Link save(Link entity) {
        Objects.requireNonNull(entity, "Entity in null");
        if (Objects.isNull(entity.getId())) {
            String key = generateKey();
            entity.setShortCode(key);
        }
        return linkRepository.save(entity);
    }

    @Override
    public LinkDto save(LinkDto dto) {
        Link link = linkMapper.dtoToEntity(dto);
        link = save(link);
        return linkMapper.entityToDto(link);
    }

    @Override
    public LinkDto updatedByShortCode(String shortCode, LinkDto dto) {
        Link link = getByShortCode(shortCode);
        if (Objects.nonNull(link)) {
            link.setUrl(dto.url());
            link = save(link);
            return linkMapper.entityToDto(link);
        }
        return null;
    }

    @Override
    public boolean deleteByShortCode(String shortCode) {
        Link link = getByShortCode(shortCode);
        if (Objects.nonNull(link)) {
            linkRepository.deleteById(link.getId());
            return true;
        }
        return false;
    }

    private Link getByShortCode(String shortCode) {
        Optional<Link> optional = linkRepository.getLinkByShortCode(shortCode);
        optional.ifPresent(this::increaseAccessCount);
        return optional.orElse(null);
    }

    @Override
    public LinkDto getDtoByShortCode(String shortCode) {
        return linkMapper.entityToDto(getByShortCode(shortCode));
    }

    @Override
    public LinkExtraDto getExtraDtoByShortCode(String shortCode) {
        return linkMapper.entityToExtraDto(getByShortCode(shortCode));
    }
}
