package com.example.demo;

import com.example.DTO.YearPopulation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PopulationController {

    private final YearPopulationService yearPopulationService;

    public PopulationController(YearPopulationService yearPopulationService) {
        this.yearPopulationService = yearPopulationService;
    }

    @GetMapping("/population")
    public ResponseEntity<?> getPopulation(@RequestParam String year) {
        int yearInt;

        try {
            yearInt = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", "Invalid 'year' parameter. It must be an integer.",
                            "example", "/population?year=2015"
                    ));
        }

    
        if (yearInt <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "error", "Invalid 'year' parameter. Year must be a positive integer."
                    ));
        }

        List<YearPopulation> result = yearPopulationService.getPopulationForYear(yearInt);

        return ResponseEntity.ok(result);
    }
}

