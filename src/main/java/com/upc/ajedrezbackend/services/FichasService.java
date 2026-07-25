package com.upc.ajedrezbackend.services;

import com.upc.ajedrezbackend.dtos.FichasDTO;
import com.upc.ajedrezbackend.entities.Fichas;
import com.upc.ajedrezbackend.interfaces.IFichasService;
import com.upc.ajedrezbackend.repositories.FichasRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FichasService implements IFichasService {
    @Autowired
    private FichasRepository fichasRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public FichasDTO crearFichas(FichasDTO fichasDTO) {
        if (fichasDTO.getId() == null) {
            Fichas fichas = modelMapper.map(fichasDTO, Fichas.class);
            fichas.setNombre("N/A");
            fichas.setPosicionX(0L);
            fichas.setPosicionY(0L);
            fichas.setEstado("Activo");
            fichasRepository.save(fichas);
            return modelMapper.map(fichas, FichasDTO.class);
        }
        return null;
    }

    @Override
    public FichasDTO actualizarFichas(FichasDTO fichasDTO) {
        if (fichasDTO.getId() != null) {
            Fichas fichas = modelMapper.map(fichasDTO, Fichas.class);
            fichas.setNombre(fichasDTO.getNombre());
            fichas.setPosicionX(fichasDTO.getPosicionX());
            fichas.setPosicionY(fichasDTO.getPosicionY());
            fichas.setEstado("Activo");
            fichasRepository.save(fichas);
            return modelMapper.map(fichas, FichasDTO.class);
        }
        return null;
    }

    @Override
    public void eliminarFichas(Long idFichas) {
        Fichas fichas = fichasRepository.findById(idFichas).orElse(null);
        if (fichas != null) {
            fichas.setEstado("Eliminado");
            fichasRepository.save(fichas);
        }
    }

    @Override
    public List<FichasDTO> buscarFichas() {
        List<Fichas> fichas = fichasRepository.findAll();
        return fichas.stream().map(ficha -> modelMapper.map(ficha, FichasDTO.class)).toList();
    }

    @Override
    public FichasDTO buscarFichasPorId(Long idFichas) {
        Fichas ficha = fichasRepository.findById(idFichas).orElse(null);
        return modelMapper.map(ficha, FichasDTO.class);
    }
}
