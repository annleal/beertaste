package com.beertaste.demo.dto;

public class BeerTapDTO {
    private Long id;
    private String name;
    private Double abv;
    private String styleName;
    private String styleColor;
    private String country;
    private int avgRating;
    private Double pricePint;
    private Double priceHalfPint;
    private String tapNumber;

    public BeerTapDTO() {} // necesario para Jackson

    public BeerTapDTO(Long id, String name, Double abv, String styleName, String styleColor,
                      String country, int avgRating, Double pricePint, Double priceHalfPint, String tapNumber) {
        this.id = id;
        this.name = name;
        this.abv = abv;
        this.styleName = styleName;
        this.styleColor = styleColor;
        this.country = country;
        this.avgRating = avgRating;
        this.pricePint = pricePint;
        this.priceHalfPint = priceHalfPint;
        this.tapNumber = tapNumber;
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getAbv() { return abv; }
    public void setAbv(Double abv) { this.abv = abv; }
    public String getStyleName() { return styleName; }
    public void setStyleName(String styleName) { this.styleName = styleName; }
    public String getStyleColor() { return styleColor; }
    public void setStyleColor(String styleColor) { this.styleColor = styleColor; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public int getAvgRating() { return avgRating; }
    public void setAvgRating(int avgRating) { this.avgRating = avgRating; }
    public Double getPricePint() { return pricePint; }
    public void setPricePint(Double pricePint) { this.pricePint = pricePint; }
    public Double getPriceHalfPint() { return priceHalfPint; }
    public void setPriceHalfPint(Double priceHalfPint) { this.priceHalfPint = priceHalfPint; }
    public String getTapNumber() { return tapNumber; }
    public void setTapNumber(String tapNumber) { this.tapNumber = tapNumber; }
}
