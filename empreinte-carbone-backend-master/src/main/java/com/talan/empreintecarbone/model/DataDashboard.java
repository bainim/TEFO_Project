package com.talan.empreintecarbone.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DataDashboard {
    private String x;
    private double y;

    public DataDashboard(String x, double y) {
        this.x = x;
        this.y = y;
    }

}
