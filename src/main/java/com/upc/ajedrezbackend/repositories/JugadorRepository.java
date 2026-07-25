package com.upc.ajedrezbackend.repositories;

import com.upc.ajedrezbackend.entities.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JugadorRepository extends JpaRepository<Jugador,Long> {
    Jugador findByCorreoElectronico(String correo);
}
