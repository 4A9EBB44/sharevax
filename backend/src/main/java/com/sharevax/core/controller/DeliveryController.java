package com.sharevax.core.controller;

import com.sharevax.core.model.dto.DeliveryDto;
import com.sharevax.core.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(summary = "Get a list of all deliveries")
    @GetMapping
    public ResponseEntity<List<DeliveryDto>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @Operation(summary = "Get a list of all incomplete deliveries")
    @GetMapping("/active")
    public ResponseEntity<List<DeliveryDto>> getActiveDeliveries() {
        return ResponseEntity.ok(deliveryService.getActiveDeliveries());
    }

    @Operation(summary = "Get a list of deliveries related to the country with {countryId}")
    @GetMapping("/country/{countryId}")
    public ResponseEntity<List<DeliveryDto>> getDeliveriesByCountry(@PathVariable Integer countryId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByCountry(countryId));
    }

    @Operation(summary = "Get the delivery with {deliveryId}")
    @GetMapping("/detail/{deliveryId}")
    public ResponseEntity<DeliveryDto> getDelivery(@PathVariable Integer deliveryId) {
        return ResponseEntity.ok(deliveryService.getDelivery(deliveryId));
    }
}
