package com.sharevax.core.service;

import com.sharevax.core.model.Delivery;
import com.sharevax.core.model.Harbor;
import com.sharevax.core.model.Suggestion;
import com.sharevax.core.model.dto.DeliveryDto;
import com.sharevax.core.repository.DeliveryRepository;

import java.util.*;

import org.locationtech.jts.geom.LineString;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RouteService routeService;

    public DeliveryService(DeliveryRepository deliveryRepository, RouteService routeService) {
        this.deliveryRepository = deliveryRepository;
        this.routeService = routeService;
    }

    public void createDelivery(Delivery delivery) {
        deliveryRepository.save(delivery);
    }

    public Delivery createDelivery(Harbor startHarbor, Harbor destinationHarbor,
                                    Date createdAt, Suggestion suggestion) {

        LineString futureRoute = routeService.findPathBetweenHarbors(startHarbor, destinationHarbor);
        LineString routeHistory = routeService.getLineString(
            new ArrayList<>(Arrays.asList(startHarbor.getCoordinate().getCoordinate())));

        // calculate the estimatedArrivalDate
        int duration = routeService.getDistanceInDays(startHarbor, destinationHarbor);
        Date estimatedArrivalDate = getEstimatedArrivalDate(duration, createdAt);

        int remainingDaysToNextHarbor = routeService.getDaysToNextStop(routeHistory, futureRoute);

        Delivery delivery = Delivery.builder()
                .startHarbor(startHarbor)
                .destinationHarbor(destinationHarbor)
                .estimatedArrivalDate(estimatedArrivalDate)
                .supply(suggestion.getSupply())
                .createdAt(createdAt)
                .deliveryStatus(Delivery.DeliveryStatus.IN_TIME)
                .demand(suggestion.getDemand())
                .routeHistory(routeHistory)
                .futureRoute(futureRoute)
                .remainingDaysToNextHarbor(remainingDaysToNextHarbor)
                .updatedAt(createdAt)
                .quantity(suggestion.getQuantity())
                .build();

        return deliveryRepository.save(delivery);
    }

    private List<DeliveryDto> mapToDto(List<Delivery> deliveries){
        return deliveries.stream().map(DeliveryDto::from).toList();
    }

    public List<DeliveryDto> getAllDeliveries() {
        var deliveries =  deliveryRepository.findAll();

        return mapToDto(getDeliveriesWithPointAddedRouteHistory(deliveries));
    }

    public List<DeliveryDto> getActiveDeliveries() {
        return getDeliveriesWithPointAddedRouteHistory(deliveryRepository.findActiveDeliveries())
                .stream().map(DeliveryDto::from).toList();
    }

    public List<Delivery> getDeliveriesWithPointAddedRouteHistory(List<Delivery> deliveries) {
        var updated = deliveries.stream().peek(delivery -> {
            LineString newRouteHistory = routeService.getLineWithAddedPoints(
                    delivery.getRouteHistory(),
                    delivery.getFutureRoute(),
                    delivery.getRemainingDaysToNextHarbor());

            delivery.setRouteHistory(newRouteHistory);
        }).toList();

        return updated;
    }

    public DeliveryDto getDelivery(Integer deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new RuntimeException("Delivery not found"));
        return mapToDto(getDeliveriesWithPointAddedRouteHistory(List.of(delivery))).stream().findFirst().orElseThrow();
    }

    public List<DeliveryDto> getDeliveriesByCountry(Integer countryId) {
        var deliveries =  deliveryRepository.findAll()
            .stream()
            .filter(delivery -> isCountrySenderOrReceiver(countryId, delivery)).toList();

        return mapToDto(getDeliveriesWithPointAddedRouteHistory(deliveries));
    }

    public List<Delivery> findActiveDeliveries() {
        return deliveryRepository.findActiveDeliveries();
    }

    public void save(Delivery delivery) {
        deliveryRepository.save(delivery);
    }

    private boolean isCountrySenderOrReceiver(Integer countryId, Delivery delivery) {
        return Objects.equals(delivery.getSupply().getId(), countryId) ||
                Objects.equals(delivery.getDemand().getId(), countryId);
    }

    private Date getEstimatedArrivalDate(int deliveryDays, Date today) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, deliveryDays + 1);
        return calendar.getTime();
    }

    public void deleteAll() {
        deliveryRepository.deleteAll();
    }

}
