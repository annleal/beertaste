package com.beertaste.demo.Services;

import com.beertaste.demo.entity.Style;
import com.beertaste.demo.repository.StyleRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class StyleService {

    private final StyleRepository styleRepository;

    public StyleService(StyleRepository styleRepository) {
        this.styleRepository = styleRepository;
    }

    public List<Style> getAllStyles() {
        return styleRepository.findAll();
    }

    public Optional<Style> getStyleById(Long id) {
        return styleRepository.findById(id);
    }

    public Style saveStyle(Style style) {
        // Validaciones básicas
        if (!StringUtils.hasText(style.getStyleName())) {
            throw new IllegalArgumentException("El campo 'styleName' no puede estar vacío");
        }
        if (!StringUtils.hasText(style.getStyleColor())) {
            throw new IllegalArgumentException("El campo 'styleColor' no puede estar vacío");
        }

        return styleRepository.save(style);
    }

    public void deleteStyle(Long id) {
        styleRepository.deleteById(id);
    }
}
