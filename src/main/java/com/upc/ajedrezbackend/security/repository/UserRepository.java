package com.upc.ajedrezbackend.security.repository;

import com.upc.ajedrezbackend.security.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO user_roles (user_id, role_id ) VALUES (:user_id, :rol_id)", nativeQuery = true)
    public Integer insertUserRol(@Param("user_id") Long user_id, @Param("rol_id") Long rol_id);
}
