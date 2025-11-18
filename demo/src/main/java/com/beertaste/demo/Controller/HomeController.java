package com.beertaste.demo.Controller;

import com.beertaste.demo.dto.BeerRankingDTO;
//import com.beertaste.demo.Services.EvaluationService;
import com.beertaste.demo.repository.EvaluationRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final EvaluationRepository evaluationRepository;

    public HomeController(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {

        // Top 5 cervezas mejor valoradas
        List<BeerRankingDTO> topValoradas = evaluationRepository.findTop5ByAverageRating(PageRequest.of(0,5));

        // Top 5 cervezas más evaluadas
        List<BeerRankingDTO> topConsumidas = evaluationRepository.findTop5MostEvaluated(PageRequest.of(0,5));

        // Top 5 cervezas mejor relación calidad/precio
        List<BeerRankingDTO> topCalidadPrecio = evaluationRepository.findTop5BestValue(PageRequest.of(0,5));

        model.addAttribute("topValoradas", topValoradas);
        model.addAttribute("topConsumidas", topConsumidas);
        model.addAttribute("topCalidadPrecio", topCalidadPrecio);

        return "home"; // renderiza templates/home.html
    }
}
