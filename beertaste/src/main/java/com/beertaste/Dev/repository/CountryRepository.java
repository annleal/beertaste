package com.beertaste.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.dev.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByCountryName(String countryName);
}
