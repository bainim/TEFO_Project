package com.talan.empreintecarbone.service.impl;

import com.talan.empreintecarbone.dto.RouteDto;
import com.talan.empreintecarbone.dto.StepDto;
import com.talan.empreintecarbone.exception.InvalidFieldException;
import com.talan.empreintecarbone.exception.ItemAlreadyUsedException;
import com.talan.empreintecarbone.exception.MandatoryFieldException;
import com.talan.empreintecarbone.model.Route;
import com.talan.empreintecarbone.model.RouteType;
import com.talan.empreintecarbone.model.Step;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.repository.RouteRepository;
import com.talan.empreintecarbone.repository.TripRepository;
import com.talan.empreintecarbone.service.RouteService;
import com.talan.empreintecarbone.service.StepService;
import com.talan.empreintecarbone.service.TransitService;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final StepService stepService;
    private final TripRepository tripRepository;
    private final TransitService transitService;

    public RouteServiceImpl(RouteRepository routeRepository, StepService stepService, TripRepository tripRepository,
            TransitService transitService) {
        this.routeRepository = routeRepository;
        this.stepService = stepService;
        this.tripRepository = tripRepository;
        this.transitService = transitService;
    }

    @Override
    public Route saveRoute(Route route) {
        return routeRepository.save(route);
    }

    @Override
    public Optional<Route> findRoute(Long id) {
        return routeRepository.findById(id);
    }

    @Override
    public List<RouteDto> getRoutes(User currentUser, Integer pageNo, Integer pageSize) {
        List<Route> routes = routeRepository.findAllByUser(currentUser, PageRequest.of(pageNo, pageSize));
        Collections.reverse(routes);
        return routes.stream().map(route -> {
            RouteDto routeDto = new RouteDto();
            routeDto.setId(route.getId());
            routeDto.setName(route.getName());
            routeDto.setType(route.getType());
            routeDto.setSteps(route.getSteps().stream().map(step -> {
                StepDto stepDto = new StepDto();
                stepDto.setId(step.getId());
                stepDto.setArrival(step.getArrival());
                stepDto.setDeparture(step.getDeparture());
                stepDto.setTransit(step.getTransit());
                stepDto.setDistance(step.getDistance());
                stepDto.setCo2(step.getCo2());
                return stepDto;
            }).collect(Collectors.toList()));
            routeDto.setCanUpdate(tripRepository.countAllByRoute(route) <= 0);
            return routeDto;
        }).collect(Collectors.toList());
    }

    @Override
    public Route updateRoute(RouteDto routeDto, Long id) {
        return routeRepository.findById(id).map(route -> {
            if (tripRepository.countAllByRoute(route) > 0) {
                throw new ItemAlreadyUsedException("This route cannot be modified because it's used by another trip");
            }
            List<Step> savedSteps = stepService.updateSteps(routeDto.getSteps(), route);
            route.setSteps(savedSteps);
            route.setName(routeDto.getName());
            return routeRepository.save(route);
        }).orElseThrow(() -> new InvalidFieldException("the route ID is invalid"));
    }

    @Override
    public void deleteRoute(Long id) {
        Optional<Route> route = routeRepository.findById(id);
        if (route.isPresent()) {
            if (tripRepository.countAllByRoute(route.get()) > 0) {
                throw new ItemAlreadyUsedException("This route cannot be deleted because it's used by another trip");
            }
            routeRepository.delete(route.get());
        } else {
            throw new InvalidFieldException("the route ID is invalid");
        }
    }

    @Override
    public void createRemoteRoute(User user) {
        Route remoteRoute = new Route();
        remoteRoute.setUser(user);
        remoteRoute.setName("Télétravail");
        remoteRoute.setType(RouteType.REMOTE);
        Route savedRoute = saveRoute(remoteRoute);
        List<StepDto> savedSteps = new ArrayList<>();
        StepDto remoteStep = new StepDto();
        remoteStep.setTransit(transitService.getRemoteTransit());
        remoteStep.setArrival("remote");
        remoteStep.setDeparture("remote");
        remoteStep.setCo2(0);
        remoteStep.setDistance(0);
        savedSteps.add(remoteStep);
        stepService.saveSteps(savedSteps, savedRoute);
    }

}
