package com.beertaste.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.demo.entity.Style;

public interface StyleRepository extends JpaRepository<Style, Long> {
    Style findByStyleName(String styleName);
}
