package com.upc.ajedrezbackend.repositories;

import com.upc.ajedrezbackend.entities.Fichas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FichasRepository extends JpaRepository<Fichas, Long> {
}
