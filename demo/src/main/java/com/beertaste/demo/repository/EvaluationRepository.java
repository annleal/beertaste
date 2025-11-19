package com.beertaste.demo.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
//import com.beertaste.demo.entity.Beer;      
import com.beertaste.demo.entity.User;      
import com.beertaste.demo.entity.Evaluation;
import com.beertaste.demo.dto.BeerRankingDTO;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

     Page<Evaluation> findByBeer_BusinessNameContainingIgnoreCase(String search, Pageable pageable);

    // Método más complejo según filtros
    Page<Evaluation> findByBeer_BusinessNameContainingIgnoreCaseAndUser_Country_CountryNameContainingIgnoreCase(
        String beerName, String countryName, Pageable pageable
    );

    Page<Evaluation> findByBeer_BusinessNameContainingIgnoreCaseAndUser(
        String beerName, User user, Pageable pageable
    );
     
    List<Evaluation> findByUser(User user);


    // Top 5 cervezas mejor valoradas
    @Query("SELECT new com.beertaste.demo.dto.BeerRankingDTO(e.beer.businessName, AVG(e.rateEvaluation)) " +
           "FROM Evaluation e " +
           "GROUP BY e.beer.idCerveza, e.beer.businessName " +
           "ORDER BY AVG(e.rateEvaluation) DESC")
    List<BeerRankingDTO> findTop5ByAverageRating(Pageable pageable);

    // Top 5 cervezas más evaluadas
    @Query("SELECT new com.beertaste.demo.dto.BeerRankingDTO(e.beer.businessName, COUNT(e)) " +
           "FROM Evaluation e " +
           "GROUP BY e.beer.idCerveza, e.beer.businessName " +
           "ORDER BY COUNT(e) DESC")
    List<BeerRankingDTO> findTop5MostEvaluated(Pageable pageable);

    // Top 5 cervezas mejor relación calidad/precio
    @Query("SELECT new com.beertaste.demo.dto.BeerRankingDTO(e.beer.businessName, AVG(e.rateEvaluation)/e.beer.abv) " +
           "FROM Evaluation e " +
           "GROUP BY e.beer.idCerveza, e.beer.businessName, e.beer.abv " +
           "ORDER BY AVG(e.rateEvaluation)/e.beer.abv DESC")
    List<BeerRankingDTO> findTop5BestValue(Pageable pageable);
}
