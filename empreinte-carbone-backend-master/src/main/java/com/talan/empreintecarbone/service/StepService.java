package com.talan.empreintecarbone.service;

import com.talan.empreintecarbone.dto.StepDto;
import com.talan.empreintecarbone.model.Step;
import com.talan.empreintecarbone.model.Route;

import java.util.List;

public interface StepService {
	List<Step> saveSteps(List<StepDto> steps, Route route);

	List<Step> updateSteps(List<StepDto> steps, Route route);
}
