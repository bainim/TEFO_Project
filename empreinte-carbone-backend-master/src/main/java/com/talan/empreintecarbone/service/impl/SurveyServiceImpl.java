package com.talan.empreintecarbone.service.impl;


import com.talan.empreintecarbone.dto.SurveyDto;
import com.talan.empreintecarbone.model.Survey;
import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.repository.SurveyRepository;
import com.talan.empreintecarbone.service.SurveyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;

    public SurveyServiceImpl(SurveyRepository surveyRepository) {
        this.surveyRepository = surveyRepository;
    }

    @Override
    public void save(List<SurveyDto> surveyDtos, User currentUser) {
        surveyDtos.forEach(surveyDto -> {
            Survey survey = new Survey();
            survey.setQuestion(surveyDto.getQuestion());
            survey.setAnswers(surveyDto.getAnswers());
            survey.setUser(currentUser);
            surveyRepository.save(survey);
        });
    }

    @Override
    public Optional<List<Survey>> findAnswer(User currentUser) {
        List<Survey> allByUser = surveyRepository.findAllByUser(currentUser);
        return Optional.ofNullable(allByUser.isEmpty() ? null : allByUser);
    }
}
