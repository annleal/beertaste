package com.beertaste.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "beers")
public class Beer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCerveza;

    @Column(nullable = false)
    private String businessName;

    private Double abv; // Alcohol By Volume

    @ManyToOne
    @JoinColumn(name = "style_id")
    private Style style;

    @ManyToOne
    @JoinColumn(name = "country_id") // nueva columna para país
    private Country country;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo; // Imagen en binario

    // Getters y Setters

    public Long getIdCerveza() { return idCerveza; }
    public void setIdCerveza(Long idCerveza) { this.idCerveza = idCerveza; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public Double getAbv() { return abv; }
    public void setAbv(Double abv) { this.abv = abv; }

    public Style getStyle() { return style; }
    public void setStyle(Style style) { this.style = style; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public byte[] getPhoto() { return photo; }
    public void setPhoto(byte[] photo) { this.photo = photo; }
}
