package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Beer;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.Services.BeerService;
import com.beertaste.demo.dto.BeerTapDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import org.imgscalr.Scalr;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/beer")
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    // ---------------- Vista Thymeleaf ----------------
    @GetMapping
    public String beerPage(Model model,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Beer> beers = beerService.searchBeersPaged(search, pageable);

        model.addAttribute("beersPage", beers);
        model.addAttribute("search", search);

        return "beer";
    }

    // ---------------- API REST paginada ----------------
    @GetMapping("/api")
    @ResponseBody
    public Page<Beer> getBeersPagedApi(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        return beerService.searchBeersPaged(search, pageable);
    }

    // ---------------- Obtener FOTO optimizada ----------------
    @GetMapping("/photo/{id}")
    public ResponseEntity<byte[]> getBeerPhoto(@PathVariable Long id) {
        Beer beer = beerService.getBeerById(id).orElse(null);

        if (beer == null || beer.getPhoto() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] photoBytes = beer.getPhoto();
            InputStream is = new ByteArrayInputStream(photoBytes);
            BufferedImage original = ImageIO.read(is);

            if (original != null) {
                BufferedImage resized = Scalr.resize(original, 400); 
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(resized, "png", baos);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_PNG);
                return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);

            } else {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                return new ResponseEntity<>(photoBytes, headers, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ---------------- Crear cerveza ----------------
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

    // ---------------- Editar cerveza ----------------
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

    // ---------------- Borrar cerveza ----------------
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

    // ---------------- API búsqueda para BeerTap ----------------
    @GetMapping("/api/search")
    @ResponseBody
    public List<BeerTapDTO> searchBeersForTap(@RequestParam String q) {
        List<Beer> beers = beerService.searchBeers(q);
        return beers.stream().map(beer -> {
            double avgRating = 0;
            if (beer.getEvaluations() != null && !beer.getEvaluations().isEmpty()) {
                avgRating = beer.getEvaluations().stream()
                                .mapToInt(e -> e.getRateEvaluation())
                                .average()
                                .orElse(0);
            }
            return new BeerTapDTO(
                    beer.getIdCerveza(),
                    beer.getBusinessName(),
                    beer.getAbv(),
                    beer.getStyle() != null ? beer.getStyle().getStyleName() : "",
                    beer.getStyle() != null ? beer.getStyle().getStyleColor() : "",
                    beer.getCountry() != null ? beer.getCountry().getCountryName() : "",
                    (int) Math.round(avgRating),
                    0.0, // pricePint inicial
                    0.0, // priceHalfPint inicial
                    ""   // tapNumber inicial
            );
        }).toList();
    }

    // ---------------- Guardar JSON local BeerTap ----------------
    @PostMapping("/tap/save")
    @ResponseBody
    public ResponseEntity<String> saveTap(@RequestBody List<BeerTapDTO> tapList) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("src/main/resources/static/tapdata.json");
            mapper.writeValue(file, tapList);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error guardando tap");
        }
    }

    @GetMapping("/tap/load")
    @ResponseBody
    public ResponseEntity<List<BeerTapDTO>> loadTap() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("src/main/resources/static/tapdata.json");
            if (!file.exists()) return ResponseEntity.ok(List.of());
            List<BeerTapDTO> tapList = List.of(mapper.readValue(file, BeerTapDTO[].class));
            return ResponseEntity.ok(tapList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }

}
