package com.example.unit_converter.services;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ConvertService {

    // Coefficients relative to 1 meter
    private final Map<String, Double> LENGTH_UNITS = Map.of(
            "mm", 0.001,
            "cm", 0.01,
            "m", 1.0,
            "km", 1000.0,
            "in", 0.254,
            "ft", 0.3048,
            "yd", 0.9144,
            "mi", 1609.344
    );

    // Coefficients relative to 1 gram
    private final Map<String, Double> WEIGHT_UNITS = Map.of(
            "mg", 0.001,
            "g", 1.0,
            "kg", 1000.0,
            "oz", 28.3495,
            "lb", 453.592
    );

    private final List<String> TEMPERATURE_UNITS = List.of("C", "K", "F");

    // Methods for checking for the extension of units
    public Boolean checkLengthUnit(String unit) {
        return LENGTH_UNITS.containsKey(unit);
    }

    public Boolean checkWeightUnit(String unit) {
        return WEIGHT_UNITS.containsKey(unit);
    }

    public Boolean checkTemperatureUnit(String unit) {
        return TEMPERATURE_UNITS.contains(unit);
    }

    // Methods for convert
    public Double convertLength(String from, String to, Double value) {
        Double value_in_meter = value * LENGTH_UNITS.get(from);
        return value_in_meter / LENGTH_UNITS.get(to);
    }

    public Double convertWeight(String from, String to, Double value) {
        Double value_in_gram = value * WEIGHT_UNITS.get(from);
        return value_in_gram / LENGTH_UNITS.get(to);
    }

    public Double convertTemperature(String from, String to, Double value) {
        //Convert value to Celsius
        Double celsius = null;
        if (from.equals("C")) {
            celsius = value;
        } else if (from.equals("F")) {
            celsius = (value - 32) * 5 / 9;
        } else if (from.equals("K")) {
            celsius = value - 273.15;
        }

        //Convert to target unit
        if (to.equals("C")) {
            return celsius;
        } else if (to.equals("F")) {
            return celsius * 9 / 5 + 32;
        } else if (to.equals("K")) {
            return celsius + 273.15;
        }

        return null;
    }
}
