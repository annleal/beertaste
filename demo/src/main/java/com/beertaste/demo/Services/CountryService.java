package com.beertaste.demo.Services;

import com.beertaste.demo.entity.Country;
import com.beertaste.demo.repository.CountryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {

    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    // Devuelve todos los países
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    // Buscar país por id (opcional)
    public Optional<Country> getCountryById(Long id) {
        return countryRepository.findById(id);
    }
}
