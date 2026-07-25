package com.upc.ajedrezbackend.interfaces;

import com.upc.ajedrezbackend.dtos.PartidaDTO;

import java.util.List;

public interface IPartidaService {
    PartidaDTO crearPartida(PartidaDTO partidaDTO);
    PartidaDTO actualizarPartida(PartidaDTO partidaDTO);
    void eliminarPartida(Long idPartida);
    List<PartidaDTO> buscarPartida();
    PartidaDTO buscarPartidaPorId(Long idPartida);
}
