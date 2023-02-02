package com.talan.empreintecarbone.api;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.talan.empreintecarbone.constant.ConfigConstant;
import com.talan.empreintecarbone.exception.InvalidFieldException;
import com.talan.empreintecarbone.model.Route;
import com.talan.empreintecarbone.model.Trip;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.service.RouteService;
import com.talan.empreintecarbone.service.TripService;
import com.talan.empreintecarbone.service.UserService;

@CrossOrigin(origins = { ConfigConstant.LOCAL_URL, ConfigConstant.SERVER_URL }, maxAge = 3600)
@RequestMapping("/api/trips")
@RestController
public class TripController {

    private final UserService userService;
    private final RouteService routeService;
    private final TripService tripService;

    public TripController(UserService userService, RouteService routeService, TripService tripService) {
        this.userService = userService;
        this.routeService = routeService;
        this.tripService = tripService;
    }

    @PostMapping(value = "/{routeId}/{date}")
    public ResponseEntity<Trip> createTrip(@PathVariable("routeId") Long routeId, @PathVariable("date") String date,
            Authentication authentication) {

        User currentUser = userService.findOne(authentication.getName());
        Optional<Route> route = routeService.findRoute(routeId);
        if (!route.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The route with ID <" + routeId + "> is not found");
        }
        Trip savedTrip = tripService.saveTrip(route.get(), date, currentUser);
        return ResponseEntity.ok(savedTrip);
    }

    @GetMapping(value = "/{pageNo}/{pageSize}")
    public ResponseEntity<List<Trip>> getTrips(Authentication authentication, @PathVariable Integer pageNo,
            @PathVariable Integer pageSize) {
        User currentUser = userService.findOne(authentication.getName());
        return ResponseEntity.ok(tripService.getTrips(currentUser, pageNo, pageSize));
    }

    @GetMapping()
    public ResponseEntity<List<Trip>> getTrips(Authentication authentication) {
        User currentUser = userService.findOne(authentication.getName());
        return ResponseEntity.ok(tripService.getTrips(currentUser, 0, 1000));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        try {
            tripService.deleteTrip(id);
        } catch (InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok("Trip Deleted");
    }

    // cumulated carbon footprint of the user between those two dates divided by the
    // distance covered during the same period of time.
    @GetMapping(value = "/footprint/{firstDate}/{lastDate}")
    public ResponseEntity<Float> getUserFootprint(@PathVariable("firstDate") String firstDate,
            @PathVariable("lastDate") String lastDate, Authentication authentication) {
        User currentUser = userService.findOne(authentication.getName());
        return ResponseEntity.ok(tripService.getUserFootprint(currentUser, firstDate, lastDate));

    }

    // comparison to the average value for users of the same group
    @GetMapping(value = "/averageFootprint/{firstDate}/{lastDate}")
    public ResponseEntity<Float> getAverageFootprint(@PathVariable("firstDate") String firstDate,
            @PathVariable("lastDate") String lastDate, Authentication authentication) {
        User currentUser = userService.findOne(authentication.getName());
        return ResponseEntity.ok(tripService.getAverageFootprint(currentUser, firstDate, lastDate));

    }
    @PostMapping(value = "/{routeId}/{routeBackId}/{date}/{userId}")
   	public ResponseEntity<Trip> createTripWithBackRoad(@PathVariable("routeId") Long routeId,
   			@PathVariable("routeBackId") Long routeBackId, @PathVariable("date") String date,
   			Authentication authentication) {

   		User currentUser = userService.findOne(authentication.getName());
   		Optional<Route> route = routeService.findRoute(routeId);
   		Optional<Route> backRoute = routeService.findRoute(routeBackId);
   		if (!route.isPresent()) {
   			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The route with ID <" + routeId + "> is not found");
   		} 
   		Trip savedTrip = tripService.saveTripWitchBackRoad(route.get(), backRoute.get(), date, currentUser);
   		return ResponseEntity.ok(savedTrip);
   	}
}
