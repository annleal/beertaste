package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Beer;
import com.beertaste.demo.Services.BeerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beers")
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @GetMapping
    public List<Beer> getAllBeers() {
        return beerService.getAllBeers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Beer> getBeerById(@PathVariable Long id) {
        return beerService.getBeerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
public ResponseEntity<?> createBeer(@RequestBody Beer beer) {
    try {
        Beer saved = beerService.saveBeer(beer);
        return ResponseEntity.ok(saved);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

@PutMapping("/{id}")
public ResponseEntity<?> updateBeer(@PathVariable Long id, @RequestBody Beer beerDetails) {
    return beerService.getBeerById(id)
            .map(beer -> {
                try {
                    beer.setBusinessName(beerDetails.getBusinessName());
                    beer.setAbv(beerDetails.getAbv());
                    beer.setStyle(beerDetails.getStyle());
                    beer.setCountry(beerDetails.getCountry()); // <-- validación implícita en el servicio
                    beer.setPhoto(beerDetails.getPhoto());
                    Beer updated = beerService.saveBeer(beer);
                    return ResponseEntity.ok(updated);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            })
            .orElse(ResponseEntity.notFound().build());
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeer(@PathVariable Long id) {
        beerService.deleteBeer(id);
        return ResponseEntity.noContent().build();
    }
}
