package com.beertaste.demo.dto;

public class BeerRankingDTO {
    private String nombre;
    private Double valor;

    // Constructor para valores Double
    public BeerRankingDTO(String nombre, Double valor) {
        this.nombre = nombre;
        this.valor = valor;
    }

    // Constructor para valores Long (como COUNT)
    public BeerRankingDTO(String nombre, Long valor) {
        this.nombre = nombre;
        this.valor = valor.doubleValue(); // convertir a Double
    }

    public String getNombre() { return nombre; }
    public Double getValor() { return valor; }
}
