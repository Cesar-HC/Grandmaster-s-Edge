package com.upc.ajedrezbackend.interfaces;

import com.upc.ajedrezbackend.dtos.FichasDTO;

import java.util.List;

public interface IFichasService {
    FichasDTO crearFichas(FichasDTO fichasDTO);
    FichasDTO actualizarFichas(FichasDTO fichasDTO);
    void eliminarFichas(Long idFichas);
    List<FichasDTO> buscarFichas();
    FichasDTO buscarFichasPorId(Long idFichas);
}
