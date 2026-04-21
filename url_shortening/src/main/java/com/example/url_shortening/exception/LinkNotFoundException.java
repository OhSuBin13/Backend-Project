package com.example.url_shortening.exception;

public class LinkNotFoundException extends RuntimeException {

    public LinkNotFoundException(String shortCode) {
        super("Link not found for shortCode: " + shortCode);
    }
}
