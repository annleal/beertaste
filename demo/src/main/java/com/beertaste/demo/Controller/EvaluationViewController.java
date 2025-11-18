package com.beertaste.demo.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.beertaste.demo.Services.EvaluationService;
import com.beertaste.demo.entity.Evaluation;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.repository.BeerRepository;
import com.beertaste.demo.repository.UserRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/evaluaciones")
public class EvaluationViewController {

    private final EvaluationService evaluationService;
    private final BeerRepository beerRepository;
    private final UserRepository userRepository;

    public EvaluationViewController(EvaluationService evaluationService,
                                    BeerRepository beerRepository,
                                    UserRepository userRepository) {
        this.evaluationService = evaluationService;
        this.beerRepository = beerRepository;
        this.userRepository = userRepository;
    }

    // LISTA CON PAGINACIÓN (ÚNICO MÉTODO)
@GetMapping
public String listEvaluations(Model model,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String country,
                              @RequestParam(required = false) Boolean mine,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "50") int size,
                              @AuthenticationPrincipal User loggedUser) {

    // Convertir página a base cero
    int pageIndex = Math.max(page - 1, 0);
    Pageable pageable = PageRequest.of(pageIndex, size);

    // Página principal
    Page<Evaluation> evaluationsPage =
            evaluationService.getPaginatedEvaluations(search, country, mine, loggedUser, pageable);

    // Mis evaluaciones (solo cuando "mine = true")
    List<Evaluation> myEvaluations =
            (mine != null && mine && loggedUser != null)
                    ? evaluationService.findFilteredEvaluations(search, country, loggedUser)
                    : List.of();

    model.addAttribute("evaluationsPage", evaluationsPage);
    model.addAttribute("myEvaluations", myEvaluations);

    // Mantener filtros
    model.addAttribute("search", search);
    model.addAttribute("country", country);
    model.addAttribute("mine", mine != null && mine);

    // Página actual en base 1 para la interfaz
    model.addAttribute("currentPage", page);

    return "evaluaciones";
}


    // Formulario para añadir evaluación
    @GetMapping("/nueva")
    public String newEvaluation(Model model) {
        model.addAttribute("evaluation", new Evaluation());
        model.addAttribute("beers", beerRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "evaluacion-form";
    }

    // Guardar evaluación
    @PostMapping("/guardar")
    public String saveEvaluation(@ModelAttribute Evaluation evaluation) {
        evaluationService.saveEvaluation(evaluation);
        return "redirect:/evaluaciones";
    }
}
