package com.beertaste.demo.Services;

import com.beertaste.demo.entity.Beer;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.repository.BeerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // Obtener todas las cervezas (sin paginación)
    public List<Beer> getAllBeers() {
        return beerRepository.findAll();
    }

    public Optional<Beer> getBeerById(Long id) {
        return beerRepository.findById(id);
    }

    // Guardar cerveza con validación de permisos
    public Beer saveBeer(Beer beer, User loggedUser) {
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

        if (beer.getIdCerveza() != null) {
            // Edición: solo propietario sin evaluaciones o admin
            Beer existing = getBeerById(beer.getIdCerveza())
                    .orElseThrow(() -> new IllegalArgumentException("Cerveza no encontrada"));

            boolean isAdmin = loggedUser.getRole().equalsIgnoreCase("ADMIN");
            boolean isOwner = existing.getUser().equals(loggedUser);
            boolean hasEvaluations = !existing.getEvaluations().isEmpty();

            if (!isAdmin && (!isOwner || hasEvaluations)) {
                throw new IllegalArgumentException("No tienes permisos para editar esta cerveza");
            }

            // Actualizar campos
            existing.setBusinessName(beer.getBusinessName());
            existing.setAbv(beer.getAbv());
            existing.setStyle(beer.getStyle());
            existing.setCountry(beer.getCountry());
            existing.setPhoto(beer.getPhoto());

            return beerRepository.save(existing);
        } else {
            // Creación: asignar usuario logueado
            beer.setUser(loggedUser);
            return beerRepository.save(beer);
        }
    }

    // Borrar cerveza con validación de permisos
    public void deleteBeer(Long id, User loggedUser) {
        Beer beer = getBeerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cerveza no encontrada"));

        boolean isAdmin = loggedUser.getRole().equalsIgnoreCase("ADMIN");
        boolean isOwner = beer.getUser().equals(loggedUser);
        boolean hasEvaluations = !beer.getEvaluations().isEmpty();

        if (!isAdmin && (!isOwner || hasEvaluations)) {
            throw new IllegalArgumentException("No tienes permisos para borrar esta cerveza");
        }

        beerRepository.delete(beer);
    }

    // Búsqueda paginada de cervezas
    public Page<Beer> searchBeersPaged(String search, Pageable pageable) {
        if (search == null || search.isEmpty()) {
            return beerRepository.findAll(pageable);
        } else {
            return beerRepository.findByBusinessNameContainingIgnoreCase(search, pageable);
        }
    }
}
