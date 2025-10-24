package com.beertaste.demo.Controller;

import com.beertaste.demo.entity.Evaluation;
import com.beertaste.demo.Services.EvaluationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping
    public List<Evaluation> getAllEvaluations() {
        return evaluationService.getAllEvaluations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evaluation> getEvaluationById(@PathVariable Long id) {
        return evaluationService.getEvaluationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createEvaluation(@RequestBody Evaluation evaluation) {
        try {
            Evaluation saved = evaluationService.saveEvaluation(evaluation);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvaluation(@PathVariable Long id, @RequestBody Evaluation evaluationDetails) {
        return evaluationService.getEvaluationById(id)
                .map(evaluation -> {
                    try {
                        evaluation.setBeer(evaluationDetails.getBeer());
                        evaluation.setUser(evaluationDetails.getUser());
                        evaluation.setPrice(evaluationDetails.getPrice());
                        evaluation.setRateEvaluation(evaluationDetails.getRateEvaluation());
                        evaluation.setDate(evaluationDetails.getDate());
                        Evaluation updated = evaluationService.saveEvaluation(evaluation);
                        return ResponseEntity.ok(updated);
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        evaluationService.deleteEvaluation(id);
        return ResponseEntity.noContent().build();
    }
}
