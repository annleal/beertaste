package com.beertaste.demo.Services;

import com.beertaste.demo.entity.Beer;
import com.beertaste.demo.repository.BeerRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class BeerService {

    private final BeerRepository beerRepository;

    public BeerService(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    public List<Beer> getAllBeers() {
        return beerRepository.findAll();
    }

    public Optional<Beer> getBeerById(Long id) {
        return beerRepository.findById(id);
    }

    public Beer saveBeer(Beer beer) {
        if (!StringUtils.hasText(beer.getBusinessName())) {
            throw new IllegalArgumentException("El campo 'businessName' no puede estar vacío");
        }
        if (beer.getAbv() == null || beer.getAbv() < 0) {
            throw new IllegalArgumentException("El campo 'abv' debe ser un número positivo");
        }
        if (beer.getStyle() == null) {
            throw new IllegalArgumentException("Debe asignarse un 'style' a la cerveza");
        }
        if (beer.getCountry() == null) {
            throw new IllegalArgumentException("Debe asignarse un 'country' a la cerveza");
        }
        return beerRepository.save(beer);
    }

    public void deleteBeer(Long id) {
        beerRepository.deleteById(id);
    }
}
