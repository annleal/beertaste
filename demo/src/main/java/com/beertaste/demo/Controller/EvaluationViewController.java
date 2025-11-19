package com.beertaste.demo.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.beertaste.demo.Services.EvaluationService;
import com.beertaste.demo.entity.Beer;
import com.beertaste.demo.entity.Evaluation;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.repository.BeerRepository;
import com.beertaste.demo.repository.UserRepository;

//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
//import java.util.List;

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
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "50") int size,
                              Principal principal) {

    String username = principal.getName();
    User loggedUser = userRepository.findByEmail(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

    // Convertir página a base cero
    int pageIndex = Math.max(page - 1, 0);
    Pageable pageable = PageRequest.of(pageIndex, size);

    // Solo las evaluaciones del usuario logueado
    Page<Evaluation> evaluationsPage = evaluationService.getPaginatedEvaluations(
            search, null, true, loggedUser, pageable
    );

    model.addAttribute("evaluationsPage", evaluationsPage);
    model.addAttribute("search", search);
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
public String saveEvaluation(
        @RequestParam Long beerId,
        @RequestParam int rateEvaluation,
        @RequestParam double price,
        @RequestParam String email) {

    // Buscar usuario por email
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con email: " + email));

    // Buscar cerveza
    Beer beer = beerRepository.findById(beerId)
            .orElseThrow(() -> new IllegalArgumentException("Cerveza no encontrada"));

    // Crear evaluación
    Evaluation evaluation = new Evaluation();
    evaluation.setBeer(beer);
    evaluation.setUser(user);
    evaluation.setRateEvaluation(rateEvaluation);
    evaluation.setPrice(price);

    evaluationService.saveEvaluation(evaluation);

    return "redirect:/evaluaciones";
}


// Mostrar formulario de edición
@GetMapping("/editar/{id}")
public String editEvaluation(@PathVariable Long id, Model model, Principal principal) {
    Evaluation evaluation = evaluationService.getEvaluationById(id)
        .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

    // Solo el propietario puede editar
    if (!evaluation.getUser().getEmail().equals(principal.getName())) {
        throw new SecurityException("No tienes permisos para editar esta evaluación");
    }

    model.addAttribute("evaluation", evaluation);
    model.addAttribute("beers", beerRepository.findAll());
    return "evaluacion-form"; // Puedes reutilizar el mismo formulario
}

// Guardar edición
@PostMapping("/editar/{id}")
public String updateEvaluation(@PathVariable Long id,
                               @RequestParam Long beerId,
                               @RequestParam int rateEvaluation,
                               @RequestParam double price,
                               Principal principal) {
    Evaluation evaluation = evaluationService.getEvaluationById(id)
        .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

    // Solo propietario
    if (!evaluation.getUser().getEmail().equals(principal.getName())) {
        throw new SecurityException("No tienes permisos para editar esta evaluación");
    }

    Beer beer = beerRepository.findById(beerId)
        .orElseThrow(() -> new IllegalArgumentException("Cerveza no encontrada"));

    evaluation.setBeer(beer);
    evaluation.setRateEvaluation(rateEvaluation);
    evaluation.setPrice(price);

    evaluationService.saveEvaluation(evaluation);
    return "redirect:/evaluaciones";
}

// Borrar evaluación
@GetMapping("/borrar/{id}")
public String deleteEvaluation(@PathVariable Long id, Principal principal) {
    Evaluation evaluation = evaluationService.getEvaluationById(id)
        .orElseThrow(() -> new IllegalArgumentException("Evaluación no encontrada"));

    // Solo propietario
    if (!evaluation.getUser().getEmail().equals(principal.getName())) {
        throw new SecurityException("No tienes permisos para borrar esta evaluación");
    }

    evaluationService.deleteEvaluation(id);
    return "redirect:/evaluaciones";
}


}
