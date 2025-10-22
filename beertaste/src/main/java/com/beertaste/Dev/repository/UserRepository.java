package com.beertaste.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.dev.entity.User;;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
