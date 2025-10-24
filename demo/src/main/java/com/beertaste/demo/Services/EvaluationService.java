package com.beertaste.demo.Services;

import com.beertaste.demo.entity.Evaluation;
import com.beertaste.demo.repository.EvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
}
