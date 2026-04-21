package com.example.url_shortening.service;

import com.example.url_shortening.dto.LinkDto;
import com.example.url_shortening.dto.LinkExtraDto;
import com.example.url_shortening.exception.LinkNotFoundException;
import com.example.url_shortening.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("dev")
class LinkServiceImplTest {

    @Autowired
    private LinkService linkService;

    @Autowired
    private LinkRepository linkRepository;

    @BeforeEach
    void setUp() {
        linkRepository.deleteAll();
    }

    @Test
    void createAndReadDoNotIncrementAccessCount() {
        LinkDto created = linkService.create(LinkDto.builder()
                .url("https://example.com")
                .build());

        assertNotNull(created.id());
        assertNotNull(created.shortCode());

        LinkDto fetched = linkService.getByShortCode(created.shortCode());
        LinkExtraDto fetchedExtra = linkService.getExtraByShortCode(created.shortCode());

        assertEquals(created.shortCode(), fetched.shortCode());
        assertEquals(0L, fetchedExtra.accessCount());
    }

    @Test
    void incrementAccessCountOnlyChangesStatistics() {
        LinkDto created = linkService.create(LinkDto.builder()
                .url("https://example.com/stats")
                .build());

        linkService.incrementAccessCount(created.shortCode());
        linkService.incrementAccessCount(created.shortCode());

        LinkExtraDto fetchedExtra = linkService.getExtraByShortCode(created.shortCode());
        assertEquals(2L, fetchedExtra.accessCount());
    }

    @Test
    void updateAndDeleteThrowWhenShortCodeDoesNotExist() {
        assertThrows(LinkNotFoundException.class, () -> linkService.updateByShortCode(
                "missing",
                LinkDto.builder().url("https://example.com/updated").build()
        ));

        assertThrows(LinkNotFoundException.class, () -> linkService.deleteByShortCode("missing"));
        assertThrows(LinkNotFoundException.class, () -> linkService.getByShortCode("missing"));
        assertThrows(LinkNotFoundException.class, () -> linkService.getExtraByShortCode("missing"));
        assertThrows(LinkNotFoundException.class, () -> linkService.incrementAccessCount("missing"));
    }

    @Test
    void deleteRemovesExistingLink() {
        LinkDto created = linkService.create(LinkDto.builder()
                .url("https://example.com/delete")
                .build());

        assertDoesNotThrow(() -> linkService.deleteByShortCode(created.shortCode()));
        assertThrows(LinkNotFoundException.class, () -> linkService.getByShortCode(created.shortCode()));
    }
}
