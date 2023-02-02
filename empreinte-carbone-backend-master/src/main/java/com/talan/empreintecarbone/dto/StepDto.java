package com.talan.empreintecarbone.dto;

import com.talan.empreintecarbone.model.Transit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class StepDto {
    private Long id;
    private String departure;
    private String arrival;
    private float distance;
    private Transit transit;
    private float co2;
}
