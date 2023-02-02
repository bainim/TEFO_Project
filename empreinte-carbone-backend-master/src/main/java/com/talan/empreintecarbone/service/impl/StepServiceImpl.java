package com.talan.empreintecarbone.service.impl;

import com.talan.empreintecarbone.dto.StepDto;
import com.talan.empreintecarbone.exception.MandatoryFieldException;
import com.talan.empreintecarbone.model.Route;
import com.talan.empreintecarbone.model.Step;
import com.talan.empreintecarbone.model.Transit;
import com.talan.empreintecarbone.repository.StepRepository;
import com.talan.empreintecarbone.service.StepService;
import com.talan.empreintecarbone.service.TransitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StepServiceImpl implements StepService {
    private final StepRepository stepRepository;
    private final TransitService transitService;

    public StepServiceImpl(StepRepository stepRepository, TransitService transitService) {
        this.stepRepository = stepRepository;
        this.transitService = transitService;
    }

    @Override
    public List<Step> saveSteps(List<StepDto> steps, Route route) {
        List<Step> savedSteps = new ArrayList<>();
        steps.forEach(stepDto -> {
            if (stepDto.getDeparture() == null || stepDto.getDeparture().trim().isEmpty()
                    || stepDto.getArrival() == null || stepDto.getArrival().trim().isEmpty()) {
                throw new MandatoryFieldException("The fields departure or arrival must not be null or empty");
            }
            if (stepDto.getDistance() == 0 && !stepDto.getTransit().getName().equals("remote")) {
                throw new MandatoryFieldException("The field distance must not be null or zero");
            }
            Transit transit = transitService.getTransit(stepDto.getTransit().getId());
            Step step = new Step();
            step.setArrival(stepDto.getArrival());
            step.setDeparture(stepDto.getDeparture());
            step.setDistance(stepDto.getDistance());
            step.setTransit(transit);
            step.setCo2(transit.getCo2() * stepDto.getDistance());
            step.setRoute(route);
            savedSteps.add(stepRepository.save(step));
        });
        return savedSteps;
    }

    @Override
    public List<Step> updateSteps(List<StepDto> steps, Route route) {
        stepRepository.deleteByRoute(route);
        return saveSteps(steps, route);
    }
}
