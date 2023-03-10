package com.sharevax.core.model.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sharevax.core.model.Delivery;
import com.sharevax.core.serializer.LineStringSerializer;
import lombok.*;
import org.locationtech.jts.geom.LineString;

import java.math.BigInteger;
import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {
    Integer deliveryId;
    HarborSummaryDto startHarbor;
    HarborSummaryDto destinationHarbor;
    String deliveryStatus;
    Date createdAt;
    BigInteger quantity;
    String urgency;
    String vaccineType;
    Date estimatedArrivalDate;

    // Optional fields
    Date updatedAt;
    Integer remainingDaysToNextHarbor;

    @JsonSerialize(using = LineStringSerializer.class)
    LineString routeHistory;

    @JsonSerialize(using = LineStringSerializer.class)
    LineString futureRoute;

    public static DeliveryDto from(Delivery delivery) {
        return DeliveryDto.builder()
                .deliveryId(delivery.getId())
                .startHarbor(HarborSummaryDto.from(delivery.getStartHarbor()))
                .destinationHarbor(HarborSummaryDto.from(delivery.getDestinationHarbor()))
                .deliveryStatus(delivery.getDeliveryStatus().toString())
                .createdAt(delivery.getCreatedAt())
                .quantity(delivery.getQuantity())
                .urgency(delivery.getDemand().getUrgency().toString())
                .vaccineType(delivery.getSupply().getVaccineType().toString())
                .estimatedArrivalDate(delivery.getEstimatedArrivalDate())
                .updatedAt(delivery.getUpdatedAt())
                .remainingDaysToNextHarbor(delivery.getDaysToNextStop())
                .routeHistory(delivery.getRouteHistory())
                .futureRoute(delivery.getFutureRoute())
                .build();
    }

}
