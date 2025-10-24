package com.beertaste.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.demo.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByCountryName(String countryName);
}
