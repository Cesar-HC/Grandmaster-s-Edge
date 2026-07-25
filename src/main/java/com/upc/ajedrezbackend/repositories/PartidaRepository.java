package com.upc.ajedrezbackend.repositories;

import com.upc.ajedrezbackend.entities.Partida;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidaRepository extends JpaRepository<Partida,Long> {
}
