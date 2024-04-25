package ru.merenkov.diploma.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.merenkov.diploma.service.CalculatorService;

@RestController
@RequestMapping("/api/v1/calculator")
@CrossOrigin(origins = "http://localhost:8080")
@AllArgsConstructor
public class CalculatorController {

    private final CalculatorService calculatorService;

    @GetMapping("/calculate-acceptable-distance")
    public double calculateAcceptableDistance(
            @RequestParam("maxWaitTime") double maxWaitTime,
            @RequestParam("processingTime") double processingTime,
            @RequestParam("propagationSpeed") double propagationSpeed) {
        return calculatorService.calculateAcceptableDistance(maxWaitTime, processingTime, propagationSpeed);
    }

    @GetMapping("/calculate-average-delay")
    public double calculateAverageDelay(
            @RequestParam("serverLoad") double serverLoad,
            @RequestParam("serverPerformance") double serverPerformance) {
        return calculatorService.calculateAverageDelay(serverLoad, serverPerformance);
    }

    @GetMapping("/calculate-traffic-density")
    public double calculateTrafficDensity(
            @RequestParam("incomingMessagesRate") double incomingMessagesRate,
            @RequestParam("serverPerformance") double serverPerformance) {
        return calculatorService.calculateTrafficDensity(incomingMessagesRate, serverPerformance);
    }

    @GetMapping("/calculate-incoming-traffic")
    public double calculateIncomingTraffic(
            @RequestParam("radius") double radius,
            @RequestParam("deviceDensity") double deviceDensity,
            @RequestParam("deviceSpeed") double deviceSpeed) {
        return calculatorService.calculateIncomingTraffic(radius, deviceDensity, deviceSpeed);
    }
}
