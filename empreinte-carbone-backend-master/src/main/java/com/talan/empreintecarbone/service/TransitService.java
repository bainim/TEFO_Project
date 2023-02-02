package com.talan.empreintecarbone.service;

import com.talan.empreintecarbone.model.Transit;

import java.util.List;

public interface TransitService {

    List<Transit> getNoRemoteTransits();

    Transit getTransit(Long id);

    Transit getRemoteTransit();
}
