package com.example.url_shortening.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Table(name = Link.TABLE_NAME, schema = "uss")
@Entity
@SequenceGenerator(name = "link_sequence_generator", sequenceName = "link_sequence_generator", allocationSize = 1)
public class Link implements Serializable {

    public static final String TABLE_NAME = "LINK";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "link_sequence_generator")
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Size(max = 2048)
    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @NotBlank
    @Size(max = 20)
    @Column(name = "short_code", nullable = false, unique = true, length = 20)
    private String shortCode;

    @Column(name = "created_date", updatable = false, nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "access_count", nullable = false)
    private long accessCount;

    public static Link create(String url) {
        Link link = new Link();
        link.updateUrl(url);
        return link;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdDate == null) {
            this.createdDate = now;
        }
        if (this.updatedDate == null) {
            this.updatedDate = now;
        }
        if (this.accessCount < 0) {
            this.accessCount = 0;
        }
    }

    public void updateUrl(String url) {
        this.url = Objects.requireNonNull(url, "url must not be null");
        markUpdated();
    }

    public void assignShortCode(String shortCode) {
        this.shortCode = Objects.requireNonNull(shortCode, "shortCode must not be null");
    }

    public void markUpdated() {
        this.updatedDate = LocalDateTime.now();
    }

    public void incrementAccessCount() {
        this.accessCount++;
    }
}
