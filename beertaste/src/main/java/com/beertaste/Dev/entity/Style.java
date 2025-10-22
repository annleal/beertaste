package com.beertaste.dev.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "styles")
public class Style {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long styleId;

    private String styleName;
    private String styleColor;

    @OneToMany(mappedBy = "style", cascade = CascadeType.ALL)
    private List<Beer> beers;

    // Getters y Setters
    public Long getStyleId() {
        return styleId;
    }

    public void setStyleId(Long styleId) {
        this.styleId = styleId;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getStyleColor() {
        return styleColor;
    }

    public void setStyleColor(String styleColor) {
        this.styleColor = styleColor;
    }

    public List<Beer> getBeers() {
        return beers;
    }

    public void setBeers(List<Beer> beers) {
        this.beers = beers;
    }
}
