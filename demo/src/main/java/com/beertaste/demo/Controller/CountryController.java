package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Country;
import com.beertaste.demo.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountryController {

    @Autowired
    private CountryRepository countryRepository;

    // GET all countries
    @GetMapping
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    // GET country by id
    @GetMapping("/{id}")
    public Country getCountryById(@PathVariable Long id) {
        return countryRepository.findById(id).orElse(null);
    }
}
