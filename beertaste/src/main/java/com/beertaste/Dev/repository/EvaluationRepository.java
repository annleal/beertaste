package com.beertaste.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.dev.entity.Evaluation;
import com.beertaste.dev.entity.User;
import com.beertaste.dev.entity.Beer;
import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByUser(User user);
    List<Evaluation> findByBeer(Beer beer);
}
