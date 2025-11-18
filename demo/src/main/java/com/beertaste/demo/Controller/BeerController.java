package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Beer;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.Services.BeerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/beer")
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    // -----------------------------
    // Vista Thymeleaf: listado de cervezas
    // -----------------------------
    @GetMapping
    public String beerPage(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Beer> beers = beerService.searchBeersPaged(search, pageable);

        model.addAttribute("beersPage", beers);
        model.addAttribute("search", search);

        return "beer"; // Thymeleaf buscará beer.html
    }

    // -----------------------------
    // API REST: listado paginado de cervezas
    // -----------------------------
    @GetMapping("/api")
    @ResponseBody
    public Page<Beer> getBeersPagedApi(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        return beerService.searchBeersPaged(search, pageable);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Beer> getBeerByIdApi(@PathVariable Long id) {
        return beerService.getBeerById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }


    // -----------------------------
    // Crear cerveza
    // -----------------------------
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<?> createBeer(@RequestBody Beer beer,
                                        @AuthenticationPrincipal User loggedUser) {
        try {
            Beer saved = beerService.saveBeer(beer, loggedUser);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -----------------------------
    // Editar cerveza
    // -----------------------------
    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> updateBeer(@PathVariable Long id,
                                        @RequestBody Beer beerDetails,
                                        @AuthenticationPrincipal User loggedUser) {
        return beerService.getBeerById(id)
                .map(existing -> {
                    try {
                        existing.setBusinessName(beerDetails.getBusinessName());
                        existing.setAbv(beerDetails.getAbv());
                        existing.setStyle(beerDetails.getStyle());
                        existing.setCountry(beerDetails.getCountry());
                        existing.setPhoto(beerDetails.getPhoto());
                        Beer updated = beerService.saveBeer(existing, loggedUser);
                        return ResponseEntity.ok(updated);
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // -----------------------------
    // Borrar cerveza
    // -----------------------------
    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteBeer(@PathVariable Long id,
                                        @AuthenticationPrincipal User loggedUser) {
        try {
            beerService.deleteBeer(id, loggedUser);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
