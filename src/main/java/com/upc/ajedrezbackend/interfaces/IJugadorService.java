package com.upc.ajedrezbackend.interfaces;

import com.upc.ajedrezbackend.dtos.JugadorDTO;

import java.util.List;

public interface IJugadorService {
    JugadorDTO crearJugador(JugadorDTO jugadorDTO);
    JugadorDTO actualizarJugador(JugadorDTO jugadorDTO);
    void eliminarJugador(Long idJugador);
    List<JugadorDTO> buscarJugadores();
    JugadorDTO buscarJugadoresPorId(Long idJugador);
    JugadorDTO buscarJugadoresPorCorreo(String correo);
}
