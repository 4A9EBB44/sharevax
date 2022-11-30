package com.sharevax.core.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "country")
public class Country {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "population", nullable = false)
  private int population;

  @Column(name = "vaccination_rate", nullable = false)
  private double vaccinationRate;

  @Column(name = "daily_vaccine_consumption", nullable = false)
  private int dailyVaccineConsumption;

  @Column(name = "vaccine_stock", nullable = false)
  private int vaccineStock;

  @Column(name = "daily_vaccine_production", nullable = false)
  private int vaccineProduction;

  @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Harbor> harbors;
}