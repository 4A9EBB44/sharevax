package com.sharevax.core.service;

import com.sharevax.core.model.Country;
import com.sharevax.core.model.Demand;
import com.sharevax.core.model.Harbor;
import com.sharevax.core.model.Supply;
import com.sharevax.core.model.event.Event;
import com.sharevax.core.model.RoutePlan;
import eu.europa.ec.eurostat.jgiscotools.feature.Feature;
import eu.europa.ec.eurostat.jgiscotools.util.GeoDistanceUtil;
import eu.europa.ec.eurostat.searoute.SeaRouting;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.locationtech.jts.geom.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteService {

    private final int SPEED_FACTOR = 200;
    private final SeaRouting seaRouting;
    private final EventService eventService;

    public RouteService(EventService eventService) {
        this.seaRouting = new SeaRouting();
        this.eventService = eventService;
    }

    public double findShortestDistanceBetweenDemandAndSupply(Demand demand, Supply supply) {
        /**
         * Find the shortest distance between a demand and a supply
         * Wrapper around getDistanceBetweenCountries for ease of use
         */
        return getDistanceBetweenCountries(demand.getCountry(), supply.getCountry());
    }

    /**
     * Returns the distance between two closest harbors of all demanding and supplying country pairs
     * @param demandingCountries
     * @param providingCountries
     * @return
     */
    public HashMap<ImmutablePair<Country, Country>, Double> findDistancesAmongCountryPairs(
            List<Country> demandingCountries, List<Country> providingCountries) {

        // pair[0] is the demanding country, pair[1] is the providing country
        HashMap<ImmutablePair<Country, Country>, Double> distances = new HashMap<>();

        for (Country demandingCountry : demandingCountries) {
            for (Country providingCountry : providingCountries) {
                var distance = getDistanceBetweenCountries(demandingCountry, providingCountry);
                distances.put(new ImmutablePair<>(demandingCountry, providingCountry), distance);
            }
        }

        return distances;
    }

    // get distance between closest pair of harbors between 2 countries
    public double getDistanceBetweenCountries(Country country, Country country2) {
        List<Harbor> country1Harbors = country.getHarbors();
        List<Harbor> country2Harbors = country2.getHarbors();

        double minDistance = Double.MAX_VALUE;
        for (Harbor harbor : country1Harbors) {
            for (Harbor harbor2 : country2Harbors) {
                double distance = getDistanceBetweenHarbors(harbor, harbor2);
                if (distance < minDistance) {
                    minDistance = distance;
                }
            }
        }
        return minDistance;
    }

    private double getDistanceBetweenHarbors(Harbor harbor, Harbor harbor2) {
        // Utilize searoute library to find distance
        Point harbor1Coordinates = harbor.getCoordinate();
        Point harbor2Coordinates = harbor2.getCoordinate();

        Feature route;
        try {
            route = getRoute(harbor1Coordinates, harbor2Coordinates);
        } catch (Exception e) {
            System.out.println("Error finding route between " + harbor.getName() + " and " + harbor2.getName());
            return Double.MAX_VALUE;
        }

        return getDistance(route);
    }


    // update the route based on ship's velocity
    public RoutePlan adaptRoute(LineString routeHistory, LineString futureRoute, Harbor destinationHarbor) {

        List<Coordinate> routeHistoryCoordinates = getCoordinates(routeHistory);
        List<Coordinate> futureRouteCoordinates = getCoordinates(futureRoute);

        int daysToNextStop = 0;

        while (daysToNextStop < 1) {
            Coordinate arriveAt = futureRouteCoordinates.get(0);
            routeHistoryCoordinates.add(arriveAt);

            boolean finalDestinationReached = futureRoute.isEmpty() || futureRoute.isRing();
            if (finalDestinationReached) {
                futureRouteCoordinates = new CoordinateList();
                break;
            }

            routeHistoryCoordinates.add(arriveAt);
            futureRouteCoordinates.remove(0);
            // update future routes
            var finalStop = futureRouteCoordinates.get(futureRouteCoordinates.size() - 1);
            if (destinationHarbor.isClosed()) {
                // if destination harbor is closed, find the closest harbor and route to it
                destinationHarbor = findClosestOpenHarbor(destinationHarbor);
                finalStop = destinationHarbor.getCoordinate().getCoordinate();
            }

            var newFutureRoute = getRoute(getPoint(arriveAt), getPoint(finalStop));
            futureRouteCoordinates = List.of(newFutureRoute.getGeometry().getCoordinates());

            var nextStop = futureRouteCoordinates.get(0);
            daysToNextStop = getDistanceInDays(getPoint(arriveAt), getPoint(nextStop));
            if (futureRouteCoordinates.size() == 1) {
                // The last stop must be taken on separately.
                // Extreme case:
                // current distance - the second last stop = 0.5 days
                // current position - last stop  = 10 days
                daysToNextStop = daysToNextStop > 0 ? daysToNextStop : 1;
                break;
            }
        }

        routeHistory = getLineString(routeHistoryCoordinates);
        futureRoute = getLineString(futureRouteCoordinates);

        return new RoutePlan(routeHistory, futureRoute, daysToNextStop, destinationHarbor);
    }

    private Harbor findClosestOpenHarbor(Harbor destinationHarbor) {
        var destinationCountry = destinationHarbor.getCountry();
        var harbors = destinationCountry.getHarbors();
        var destinationCoordinate = destinationHarbor.getCoordinate().getCoordinate();
        var minDistance = Integer.MAX_VALUE;
        Harbor closestHarbor = null;
        for (Harbor harbor : harbors) {
            if (harbor.isClosed()) {
                continue;
            }
            var harborCoordinate = harbor.getCoordinate().getCoordinate();
            var distance = getDistanceInDays(getPoint(destinationCoordinate), getPoint(harborCoordinate));
            if (distance < minDistance) {
                minDistance = distance;
                closestHarbor = harbor;
            }
        }
        return closestHarbor;
    }

    public List<Coordinate> getCoordinates(LineString lineString) {
        return new ArrayList<>(Arrays.asList(lineString.getCoordinates()));
    }

    private Point getPoint(Coordinate coordinate) {
        return new GeometryFactory().createPoint(coordinate);
    }

    public LineString getLineString(List<Coordinate> coordinates) {
        if (coordinates.size() == 1) {
            coordinates.add(coordinates.get(0));
        }
        return new GeometryFactory().createLineString(
            coordinates.toArray(new Coordinate[0]));
    }

    public LineString getLineString(Harbor startHarbor, Harbor endHarbor) {
        return getLineString(startHarbor.getCoordinate(), endHarbor.getCoordinate());
    }

    public LineString getLineString(Point p1, Point p2) {

        // turn the Route into LineString
        Feature route = getRoute(p1, p2);

        // insert points
        Coordinate[] coordinates = route.getGeometry().getCoordinates();
        List<Coordinate> coordinatesList = new ArrayList<Coordinate>();
        for (int i = 0; i < coordinates.length - 1; i++) {
            Coordinate start = coordinates[i];
            Coordinate next = coordinates[i + 1];
            coordinatesList.add(start);
            int duration = getDistanceInDays(getPoint(start), getPoint(next));
            int pointsToInsert = duration - 1;
            int j = 0;
            while (j < pointsToInsert) {
                Coordinate coordinate = getMiddlePoint(start, next, j, pointsToInsert);
                coordinatesList.add(coordinate);
                j++;
            }
        }
        coordinatesList.add(coordinates[coordinates.length - 1]);

        // remove the first one
        coordinatesList.remove(0);

        return getLineString(coordinatesList);
    }

    /**
     *
     * @param start
     * @param end
     * @param index [0,..] The n th point between start and end
     * @param totalPoints total points between start and end
     */
    private Coordinate getMiddlePoint(Coordinate start, Coordinate end, int index,
        int totalPoints) {
        double xUnit = (end.getX() - start.getX()) / totalPoints;
        double yUnit = (end.getY() - start.getY()) / totalPoints;

        double x = start.getX() + xUnit * (index + 1);
        double y = start.getY() + yUnit * (index + 1);

        return new Coordinate(x, y);
    }

    private Feature getRoute(Point start, Point end) {
        double long1 = start.getX();
        double lat1 = start.getY();

        double long2 = end.getX();
        double lat2 = end.getY();

        // find all blocked paths
        List<Event> activeEvents = eventService.getActiveEvents();
        Set<String> blockedStraitNames = eventService.findBlockedStraits(activeEvents);
        Set<String> blockedPassageNames = eventService.findBlockedPassages(activeEvents);
        Set<String> blockedChannelNames = eventService.findBlockedChannels(activeEvents);
        return seaRouting.getRoute(long1, lat1, long2, lat2,
                blockedChannelNames.contains("SUEZ"), blockedChannelNames.contains("PANAMA"),
                blockedStraitNames.contains("MALACCA"), blockedStraitNames.contains("GIBRALTAR"), blockedStraitNames.contains("DOVER"),
                blockedStraitNames.contains("BERING"), blockedStraitNames.contains("MAGELLAN"), blockedStraitNames.contains("BAB_EL_MANDEB"),
                blockedChannelNames.contains("KIEL"), blockedChannelNames.contains("CORINTH"),
                blockedPassageNames.contains("NORTHWEST"), blockedPassageNames.contains("NORTHEAST"));
    }

    private double getDistance(Feature route) {
        MultiLineString geometry = (MultiLineString) route.getGeometry();
        return GeoDistanceUtil.getLengthGeoKM(geometry);
    }


    public int getDaysToNextStop(LineString routeHistory, LineString futureRoute) {
        if (futureRoute.isEmpty()) {
            return 0;
        }

        Point arriveAt = routeHistory.getEndPoint();
        Point nextStop = futureRoute.getStartPoint();

        int duration = getDistanceInDays(arriveAt, nextStop);

        // Automatic correction for two points being too close together
        duration = duration > 0 ? duration : 1;

        return duration;
    }

    public int getDistanceInDays(Harbor harbor, Harbor harbor2) {
        return getDistanceInDays(harbor.getCoordinate(), harbor2.getCoordinate());
    }

    public int getDistanceInDays(Point start, Point end) {
        double distance = getDistance(getRoute(start, end));
        int days = (int) distance / SPEED_FACTOR;
        return days;
    }

}
