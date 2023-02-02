package com.talan.empreintecarbone.service.impl;

import com.talan.empreintecarbone.exception.InvalidFieldException;
import com.talan.empreintecarbone.model.Transit;
import com.talan.empreintecarbone.repository.TransitRepository;
import org.springframework.stereotype.Service;

import com.talan.empreintecarbone.service.TransitService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransitServiceImpl implements TransitService {

    private final TransitRepository transitRepository;
    // this String is used to do the mapping with the DB.
    private final String remoteString = "remote";

    public TransitServiceImpl(TransitRepository transitRepository) {
        this.transitRepository = transitRepository;
    }

    @Override
    public List<Transit> getNoRemoteTransits() {
        return transitRepository.findByNameNot(remoteString);

    }

    @Override
    public Transit getTransit(Long id) {
        Optional<Transit> transit = transitRepository.findById(id);
        return transit.orElseThrow(() -> new InvalidFieldException("The ID of the transit is invalid."));
    }

    @Override
    public Transit getRemoteTransit() {
        for (Transit transit : transitRepository.findAll()) {
            if (transit.getName().equals(remoteString)) {
                return transit;
            }
        }
        return null;
    }
}
