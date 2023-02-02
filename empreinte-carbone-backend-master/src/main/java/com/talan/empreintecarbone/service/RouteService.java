package com.talan.empreintecarbone.service;

import com.talan.empreintecarbone.dto.RouteDto;
import com.talan.empreintecarbone.model.Route;
import com.talan.empreintecarbone.model.User;

import java.util.List;
import java.util.Optional;

public interface RouteService {
    Route saveRoute(Route route);

    Optional<Route> findRoute(Long id);

    List<RouteDto> getRoutes(User currentUser, Integer pageNo, Integer pageSize);

    Route updateRoute(RouteDto route, Long id);

    void deleteRoute(Long id);

    void createRemoteRoute(User user);
}
