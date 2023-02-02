package com.talan.empreintecarbone.api;


import java.util.*;

import com.talan.empreintecarbone.dto.RouteDto;
import com.talan.empreintecarbone.exception.InvalidFieldException;
import com.talan.empreintecarbone.exception.ItemAlreadyUsedException;
import com.talan.empreintecarbone.exception.MandatoryFieldException;
import com.talan.empreintecarbone.model.Step;
import com.talan.empreintecarbone.service.StepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.talan.empreintecarbone.constant.ConfigConstant;
import com.talan.empreintecarbone.model.Route;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.service.RouteService;
import com.talan.empreintecarbone.service.UserService;
import org.springframework.web.server.ResponseStatusException;

@CrossOrigin(origins = {ConfigConstant.LOCAL_URL, ConfigConstant.SERVER_URL}, maxAge = 3600)
@RequestMapping("/api/routes")
@RestController
public class RouteController {

    private final StepService stepService;
	private final RouteService routeService;
	private final UserService userService;

    public RouteController(StepService stepService, RouteService routeService, UserService userService) {
        this.stepService = stepService;
        this.routeService = routeService;
        this.userService = userService;
    }

    @PostMapping()
    public ResponseEntity<Route> createRoute(@RequestBody RouteDto routeDto, Authentication authentication) {
        User currentUser = userService.findOne(authentication.getName());

        Route route = new Route();
        route.setUser(currentUser);
        route.setName(routeDto.getName());
        route.setType(routeDto.getType());
        Route savedRoute = routeService.saveRoute(route);
        List<Step> savedSteps = null;
        try {
            savedSteps = stepService.saveSteps(routeDto.getSteps(), savedRoute);
        } catch (MandatoryFieldException|InvalidFieldException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
        }
        savedRoute.setSteps(savedSteps);
        return ResponseEntity.ok(savedRoute);
    }

    @GetMapping(value = "/{idRoute}")
    public ResponseEntity<Route> getRoute(@PathVariable("idRoute") Long idRoute) {
        Optional<Route> route = routeService.findRoute(idRoute);
        if (!route.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The route with ID <" + idRoute + "> is not found");
        }
        return ResponseEntity.ok(route.get());
    }

    @GetMapping(value = "/{pageNo}/{pageSize}")
    public ResponseEntity<List<RouteDto>> getRoutes(Authentication authentication, @PathVariable Integer pageNo,
                                                 @PathVariable Integer pageSize) {
        User currentUser = userService.findOne(authentication.getName());
        return ResponseEntity.ok(routeService.getRoutes(currentUser, pageNo, pageSize));
    }

    @GetMapping()
    public ResponseEntity<List<RouteDto>> getRoutes(Authentication authentication) {
        User currentUser = userService.findOne(authentication.getName());
        return ResponseEntity.ok(routeService.getRoutes(currentUser, 0, 1000));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Route> updateRoute(@RequestBody RouteDto route, @PathVariable Long id) {
        Route updatedRoute;
        try {
            updatedRoute = routeService.updateRoute(route, id);
        } catch (InvalidFieldException|ItemAlreadyUsedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok(updatedRoute);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoute(@PathVariable Long id) {
        try {
            routeService.deleteRoute(id);
        } catch (InvalidFieldException|ItemAlreadyUsedException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
        return ResponseEntity.ok("deleted");
    }
}
