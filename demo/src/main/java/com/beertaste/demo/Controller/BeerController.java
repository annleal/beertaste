package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Beer;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.Services.BeerService;

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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.imgscalr.Scalr;

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

          if (beer == null) {
        System.out.println("Cerveza no encontrada: id=" + id);
        return ResponseEntity.notFound().build();
    }

        if (beer == null || beer.getPhoto() == null) {
            System.out.println("La cerveza no tiene foto: id=" + id);
            return ResponseEntity.notFound().build();
        }
        System.out.println("Foto encontrada: tamaño=" + beer.getPhoto().length + " bytes");
        


       try {
    byte[] photoBytes = beer.getPhoto();
    InputStream is = new ByteArrayInputStream(photoBytes);
    BufferedImage original = ImageIO.read(is);

    if (original != null) {
        // Si ImageIO puede leer la imagen, la redimensionamos
        BufferedImage resized = Scalr.resize(original, 400); // ancho máximo 400px

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, "png", baos);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);

    } else {
        // Si no puede leer, enviamos los bytes tal cual
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<>(photoBytes, headers, HttpStatus.OK);
    }

} catch (Exception e) {
    e.printStackTrace(); // para ver la causa en consola
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
}
