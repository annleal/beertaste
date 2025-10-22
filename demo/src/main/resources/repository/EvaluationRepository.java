package com.beertaste.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.demo.entity.Evaluation;
import com.beertaste.demo.entity.User;
import com.beertaste.demo.entity.Beer;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByUser(User user);
    List<Evaluation> findByBeer(Beer beer);
}