package com.example.url_shortening.service;

import com.example.url_shortening.dto.LinkDto;
import com.example.url_shortening.dto.LinkExtraDto;
import com.example.url_shortening.entity.Link;
import com.example.url_shortening.exception.LinkNotFoundException;
import com.example.url_shortening.mapper.LinkMapper;
import com.example.url_shortening.repository.LinkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Objects;

@Service
public class LinkServiceImpl implements LinkService {
    private static final int SHORT_CODE_LENGTH = 5;
    private static final String KEY_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final LinkRepository linkRepository;
    private final LinkMapper linkMapper;

    public LinkServiceImpl(LinkRepository linkRepository,
                           LinkMapper linkMapper) {
        this.linkRepository = linkRepository;
        this.linkMapper = linkMapper;
    }

    private String generateKey() {
        String key;
        do {
            key = generateRandomKey(SHORT_CODE_LENGTH);
        } while (this.linkRepository.existsByShortCode(key));
        return key;
    }

    private String generateRandomKey(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(KEY_CHARACTERS.length());
            builder.append(KEY_CHARACTERS.charAt(index));
        }
        return builder.toString();
    }

    private Link getExistingLink(String shortCode) {
        return linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));
    }

    @Override
    @Transactional
    public LinkDto create(LinkDto dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        Link link = linkMapper.createEntity(dto);
        link.assignShortCode(generateKey());
        link = linkRepository.save(link);
        return linkMapper.entityToDto(link);
    }

    @Override
    @Transactional
    public LinkDto updateByShortCode(String shortCode, LinkDto dto) {
        Objects.requireNonNull(dto, "dto must not be null");

        Link link = getExistingLink(shortCode);
        link.updateUrl(dto.url());
        link = linkRepository.save(link);
        return linkMapper.entityToDto(link);
    }

    @Override
    @Transactional
    public void deleteByShortCode(String shortCode) {
        Link link = getExistingLink(shortCode);
        linkRepository.deleteById(link.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public LinkDto getByShortCode(String shortCode) {
        return linkMapper.entityToDto(getExistingLink(shortCode));
    }

    @Override
    @Transactional(readOnly = true)
    public LinkExtraDto getExtraByShortCode(String shortCode) {
        return linkMapper.entityToExtraDto(getExistingLink(shortCode));
    }

    @Override
    @Transactional
    public void incrementAccessCount(String shortCode) {
        Link link = getExistingLink(shortCode);
        link.incrementAccessCount();
        linkRepository.save(link);
    }
}
