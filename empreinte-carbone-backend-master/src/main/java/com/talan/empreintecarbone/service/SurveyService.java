package com.talan.empreintecarbone.service;

import com.talan.empreintecarbone.dto.SurveyDto;
import com.talan.empreintecarbone.model.Survey;
import com.talan.empreintecarbone.model.User;

import java.util.List;
import java.util.Optional;

public interface SurveyService {
    void save(List<SurveyDto> surveys, User currentUser);
    Optional<List<Survey>> findAnswer(User currentUser);
}
