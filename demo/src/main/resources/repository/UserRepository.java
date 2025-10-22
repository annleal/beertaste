package com.beertaste.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beertaste.demo.entity.User;;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
