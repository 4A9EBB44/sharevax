package com.sharevax.core.controller;

import com.sharevax.core.service.SimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simulation")
public class SimulationController {

    private final SimulationService simulationService;

    public SimulationController(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PatchMapping("/increment-day")
    public void incrementDay() {
        simulationService.simulateDay();
    }

    @GetMapping("/current-day")
    public ResponseEntity<Integer> getDay() {
        return ResponseEntity.ok(simulationService.getDay());
    }
}