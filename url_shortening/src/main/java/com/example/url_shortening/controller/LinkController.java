package com.example.url_shortening.controller;

import com.example.url_shortening.dto.LinkDto;
import com.example.url_shortening.dto.LinkExtraDto;
import com.example.url_shortening.service.LinkService;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shorten")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public ResponseEntity<@NonNull LinkDto> createNewLink(@RequestBody(required = false) LinkDto dto) {
        if (hasInvalidUrl(dto)) {
            return ResponseEntity.badRequest().build();
        }
        return new ResponseEntity<>(linkService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{shortCode}")
    public ResponseEntity<@NonNull LinkDto> updateLink(@RequestBody(required = false) LinkDto dto,
                                                       @PathVariable("shortCode") @NotNull String shortCode) {
        if (hasInvalidUrl(dto)) {
            return ResponseEntity.badRequest().build();
        }
        LinkDto result = linkService.updateByShortCode(shortCode, dto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<@NonNull LinkDto> getByShortCode(@PathVariable @NotNull String shortCode) {
        LinkDto result = linkService.getByShortCode(shortCode);
        linkService.incrementAccessCount(shortCode);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{shortCode}")
    public ResponseEntity<@NonNull Void> deleteByShortCode(@PathVariable String shortCode) {
        linkService.deleteByShortCode(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<@NonNull LinkExtraDto> getLinkWithStats(@PathVariable("shortCode") @NotNull String shortCode) {
        LinkExtraDto result = linkService.getExtraByShortCode(shortCode);
        return ResponseEntity.ok(result);
    }

    private boolean hasInvalidUrl(LinkDto dto) {
        return dto == null || !StringUtils.hasText(dto.url());
    }
}
