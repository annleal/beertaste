package com.beertaste.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.dev.entity.Style;

public interface StyleRepository extends JpaRepository<Style, Long> {
    Style findByStyleName(String styleName);
}
