package ru.merenkov.diploma.service;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class CalculatorService {

    public double calculateAcceptableDistance(@PositiveOrZero double maxWaitTime,
                                              @PositiveOrZero double processingTime,
                                              @Positive double propagationSpeed) {
        return (maxWaitTime - processingTime) / (2 * propagationSpeed);
    }

    public double calculateAverageDelay(@DecimalMin(value = "0")
                                        @DecimalMax(value = "1", inclusive = false) double serverLoad,
                                        @Positive double serverPerformance) {
        return (serverLoad / (1 - serverLoad)) * (1 / serverPerformance);
    }

    public double calculateTrafficDensity(@PositiveOrZero double incomingMessagesRate,
                                          @Positive double serverPerformance) {
        return incomingMessagesRate / serverPerformance;
    }

    public double calculateIncomingTraffic(@Positive double radius,
                                           @Positive double deviceDensity,
                                           @Positive double deviceSpeed) {
        return 2 * radius * deviceDensity * deviceSpeed;
    }
}
