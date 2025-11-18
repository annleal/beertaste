package com.beertaste.demo.Services;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.beertaste.demo.entity.Evaluation;
import com.beertaste.demo.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.beertaste.demo.entity.User;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Service
public class EvaluationService {
  

    private final EvaluationRepository evaluationRepository;

    public EvaluationService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    public Optional<Evaluation> getEvaluationById(Long id) {
        return evaluationRepository.findById(id);
    }

    public Evaluation saveEvaluation(Evaluation evaluation) {
        Assert.notNull(evaluation.getUser(), "Debe asignarse un 'user' a la evaluación");
        Assert.notNull(evaluation.getBeer(), "Debe asignarse un 'beer' a la evaluación");
        Assert.notNull(evaluation.getPrice(), "El campo 'price' no puede estar vacío");
        Assert.notNull(evaluation.getRateEvaluation(), "El campo 'rateEvaluation' no puede estar vacío");

        return evaluationRepository.save(evaluation);
    }

    public void deleteEvaluation(Long id) {
        evaluationRepository.deleteById(id);
    }

    

    public List<Evaluation> searchEvaluations(String keyword) {
    return evaluationRepository.findAll().stream()
            .filter(e -> e.getBeer().getBusinessName().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());
}

    public List<Evaluation> findFilteredEvaluations(String keyword, String country, User user) {
    return evaluationRepository.findAll().stream()
            .filter(e -> (keyword == null || e.getBeer().getBusinessName().toLowerCase().contains(keyword.toLowerCase())))
            .filter(e -> (country == null || e.getBeer().getCountry().toString().equalsIgnoreCase(country)))
            .filter(e -> (user == null || e.getUser().equals(user)))
            .collect(Collectors.toList());
}

    public Page<Evaluation> getPaginatedEvaluations(String search, String country, Boolean mine, User loggedUser, Pageable pageable) {
    if (mine != null && mine) {
        return evaluationRepository.findByBeer_BusinessNameContainingIgnoreCaseAndUser(search, loggedUser, pageable);
    } else if (country != null && !country.isEmpty()) {
        return evaluationRepository.findByBeer_BusinessNameContainingIgnoreCaseAndUser_Country_CountryNameContainingIgnoreCase(search, country, pageable);
    } else {
        return evaluationRepository.findByBeer_BusinessNameContainingIgnoreCase(search, pageable);
    }
    }

}
