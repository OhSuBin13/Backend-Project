package com.example.url_shortening.controller;

import com.example.url_shortening.exception.LinkNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LinkExceptionHandler {

    @ExceptionHandler(LinkNotFoundException.class)
    public ProblemDetail handleLinkNotFound(LinkNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
        problemDetail.setTitle("Link not found");
        return problemDetail;
    }
}
