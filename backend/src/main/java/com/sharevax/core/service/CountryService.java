package com.sharevax.core.service;

import com.sharevax.core.model.Country;
import com.sharevax.core.model.dto.HomeSummaryDto;
import com.sharevax.core.repository.CountryRepository;
import com.sharevax.core.util.CasingUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Country getCountryById(Integer countryId) {
        return countryRepository.findById(countryId).orElseThrow(() -> new RuntimeException("Country not found"));
    }

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public HomeSummaryDto getHomeCountrySummary(String countryName) {
        String nameToCamelCase = CasingUtil.toCamelCase(countryName);
        Country country = countryRepository.findByName(nameToCamelCase).orElseThrow(() -> new RuntimeException("Country not found"));
        return new HomeSummaryDto(country.getName(), country.getDailyVaccineProduction(),
                country.getDailyVaccineConsumption());
    }
}