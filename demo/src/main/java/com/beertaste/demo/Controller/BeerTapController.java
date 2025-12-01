package com.beertaste.demo.Controller;

import com.beertaste.demo.dto.BeerTapDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@Controller
@RequestMapping("/beertap")
public class BeerTapController {

    private final String TAPDATA_PATH = "src/main/resources/static/tapdata.json";

    // ---------------- PÁGINA HTML ----------------
    @GetMapping
    public String beertapPage() {
        return "beertap";   // carga templates/beertap.html
    }

    // ---------------- Cargar JSON ----------------
    @GetMapping("/load")
    @ResponseBody
    public List<BeerTapDTO> loadTapData() throws Exception {
        File file = new File(TAPDATA_PATH);
        ObjectMapper mapper = new ObjectMapper();

        if (file.exists()) {
            return List.of(mapper.readValue(file, BeerTapDTO[].class));
        } else {
            return List.of();
        }
    }

    // ---------------- Guardar JSON ----------------
    @PostMapping("/save")
    @ResponseBody
    public String saveTapData(@RequestBody List<BeerTapDTO> beers) throws Exception {
        File file = new File(TAPDATA_PATH);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, beers);
        return "OK";
    }
}
