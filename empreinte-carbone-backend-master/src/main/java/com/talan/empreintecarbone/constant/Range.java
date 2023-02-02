package com.talan.empreintecarbone.constant;

import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

public enum Range {
    DAY("day", TemporalAdjusters.ofDateAdjuster(d -> d)),
    WEEK("week", TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
    TRIMESTER("trimester", TemporalAdjusters.previousOrSame(DayOfWeek.of(1))),
    YEAR("year", TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

    private final String value;
    private final TemporalAdjuster temporalAdjusters;

    private Range(String value, TemporalAdjuster temporalAdjusters) {
        this.value = value;
        this.temporalAdjusters = temporalAdjusters;
    }

    public String getValue() {
        return value;
    }

    public TemporalAdjuster getTemporalAdjusters() {
        return temporalAdjusters;
    }
}
