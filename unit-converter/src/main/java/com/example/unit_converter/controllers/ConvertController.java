package com.example.unit_converter.controllers;

import com.example.unit_converter.dto.RequestDto;
import com.example.unit_converter.services.ConvertService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/convert")
@AllArgsConstructor
public class ConvertController {

    private final ConvertService convertService;

    @PostMapping("/length")
    public ResponseEntity<?> convertLength(@RequestBody RequestDto request) {
        if (!convertService.checkLengthUnit(request.getFrom()) || !convertService.checkLengthUnit(request.getTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect values");
        }
        Double result = convertService.convertLength(request.getFrom(), request.getTo(), request.getValue());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/weight")
    public ResponseEntity<?> convertWeight(@RequestBody RequestDto request) {
        if (!convertService.checkWeightUnit(request.getFrom()) || !convertService.checkWeightUnit(request.getTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect values");
        }
        Double result = convertService.convertWeight(request.getFrom(), request.getTo(), request.getValue());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/temperature")
    public ResponseEntity<?> convertTemperature(@RequestBody RequestDto request) {
        if (!convertService.checkTemperatureUnit(request.getFrom()) || !convertService.checkTemperatureUnit(request.getTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect values");
        }
        Double result = convertService.convertTemperature(request.getFrom(), request.getTo(), request.getValue());
        return ResponseEntity.ok(result);
    }
}
