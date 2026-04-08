package com.example.unit_converter.dto;

import lombok.Data;

@Data
public class RequestDto {

    private String from;
    private String to;
    private Double value;
}
