package com.talan.empreintecarbone.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.talan.empreintecarbone.model.RouteType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RouteDto {
    private Long id;
    private String name;
    private RouteType type;
    private List<StepDto> steps;

    private Boolean canUpdate;
}
