package com.talan.empreintecarbone.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class SurveyDto implements Serializable {
    private String question;
    private List<String> answers;
}
