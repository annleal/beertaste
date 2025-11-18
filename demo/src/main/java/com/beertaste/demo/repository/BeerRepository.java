package com.beertaste.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.demo.entity.Beer;
import com.beertaste.demo.entity.Style;
import com.beertaste.demo.entity.Country;
import java.util.List;

public interface BeerRepository extends JpaRepository<Beer, Long> {

    List<Beer> findByStyle(Style style);

    List<Beer> findByCountry(Country country);

    List<Beer> findByBusinessNameContainingIgnoreCase(String name);

    // Método para búsqueda paginada
    Page<Beer> findByBusinessNameContainingIgnoreCase(String name, Pageable pageable);
}
