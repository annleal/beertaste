package com.beertaste.dev.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "evaluations")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEvaluation;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_cerveza")
    private Beer beer;

    private Double price;
    private Integer rateEvaluation;

    private LocalDate date = LocalDate.now();

    // Getters y Setters
    public Long getIdEvaluation() {
        return idEvaluation;
    }

    public void setIdEvaluation(Long idEvaluation) {
        this.idEvaluation = idEvaluation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getRateEvaluation() {
        return rateEvaluation;
    }

    public void setRateEvaluation(Integer rateEvaluation) {
        this.rateEvaluation = rateEvaluation;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
