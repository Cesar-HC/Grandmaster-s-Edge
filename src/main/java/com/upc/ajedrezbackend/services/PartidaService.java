package com.upc.ajedrezbackend.services;

import com.upc.ajedrezbackend.dtos.PartidaDTO;
import com.upc.ajedrezbackend.entities.Partida;
import com.upc.ajedrezbackend.interfaces.IPartidaService;
import com.upc.ajedrezbackend.repositories.PartidaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class PartidaService implements IPartidaService {
    @Autowired
    private PartidaRepository partidaRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PartidaDTO crearPartida(PartidaDTO partidaDTO) {
        if (partidaDTO.getId() == null) {
            Partida partida = modelMapper.map(partidaDTO, Partida.class);
            partida.setFecha(LocalDate.now());
            partida.setDuracion(LocalTime.of(0, 0, 0));
            partida.setNombreGanador("N/A");
            partida.setEloGanadoOPerdido(0.0);
            partida.setJugador(partidaDTO.getJugador());
            partida.setFichas(partidaDTO.getFichas());
            partida.setFichasEliminadas("N/A");
            partida.setEstado("En curso");
            partidaRepository.save(partida);
            return modelMapper.map(partida, PartidaDTO.class);
        }
        return null;
    }

    @Override
    public PartidaDTO actualizarPartida(PartidaDTO partidaDTO) {
        if (partidaDTO.getId() != null) {
            Partida partida = modelMapper.map(partidaDTO, Partida.class);
            partida.setFecha(LocalDate.now());
            partida.setDuracion(partidaDTO.getDuracion());
            partida.setNombreGanador(partidaDTO.getNombreGanador());
            partida.setEloGanadoOPerdido(partidaDTO.getEloGanadoOPerdido());
            partida.setJugador(partidaDTO.getJugador());
            partida.setFichas(partidaDTO.getFichas());
            partida.setFichasEliminadas(partidaDTO.getFichasEliminadas());
            partida.setEstado("Finalizado");
            partidaRepository.save(partida);
            return modelMapper.map(partida, PartidaDTO.class);
        }
        return null;
    }

    @Override
    public void eliminarPartida(Long idPartida) {
        Partida partida = partidaRepository.findById(idPartida).orElse(null);
        if (partida != null) {
            partida.setEstado("Eliminado");
            partidaRepository.save(partida);
        }
    }

    @Override
    public List<PartidaDTO> buscarPartida() {
        List<Partida> partidas = partidaRepository.findAll();
        return partidas.stream().map(partida -> modelMapper.map(partida, PartidaDTO.class)).toList();
    }

    @Override
    public PartidaDTO buscarPartidaPorId(Long idPartida) {
        Partida partida = partidaRepository.findById(idPartida).orElse(null);
        return modelMapper.map(partida, PartidaDTO.class);
    }
}
