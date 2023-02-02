package com.talan.empreintecarbone.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Setter
@Getter
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private float footprintCarbone;
    private LocalDate date;
    @ManyToOne
    private User user;
    @ManyToOne
    private Route route;
    @ManyToOne
    @JoinColumn(name = "back_route_id", nullable = true)
    private Route backRoute;
    @Transient
    private double sumDistances;
    @Transient
    private double co2ByKilometre;
}
