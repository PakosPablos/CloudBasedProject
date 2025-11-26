package com.example.demo;

import com.example.DTO.YearPopulation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PopulationController {

    private final YearPopulationService yearPopulationService;

    // ✅ Spring injects the service here
    public PopulationController(YearPopulationService yearPopulationService) {
        this.yearPopulationService = yearPopulationService;
    }

    // Only functionality: input = year, output = JSON
    @GetMapping("/population")
    public List<YearPopulation> getPopulation(@RequestParam int year) {
        // ✅ IMPORTANT: use the *field* (lowercase y), not the class name
        return yearPopulationService.getPopulationForYear(year);
    }
}
