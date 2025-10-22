package com.beertaste.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.dev.entity.Beer;
import com.beertaste.dev.entity.Style;
import com.beertaste.dev.entity.Country;
import java.util.List;

public interface BeerRepository extends JpaRepository<Beer, Long> {
    List<Beer> findByStyle(Style style);
    List<Beer> findByCountryOfOrigin(Country country);
    List<Beer> findByBusinessNameContainingIgnoreCase(String name);
}
