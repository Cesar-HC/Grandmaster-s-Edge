package com.upc.ajedrezbackend.security.repository;

import com.upc.ajedrezbackend.security.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
