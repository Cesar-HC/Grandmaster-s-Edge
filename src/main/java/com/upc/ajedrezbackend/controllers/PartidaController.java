package com.upc.ajedrezbackend.controllers;

import com.upc.ajedrezbackend.dtos.PartidaDTO;
import com.upc.ajedrezbackend.interfaces.IPartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", exposedHeaders = "Authorization")
@RequestMapping("/api")
public class PartidaController {
    @Autowired
    private IPartidaService iPartidaService;
    @PostMapping("/partida")
    public PartidaDTO crearPartida(@RequestBody PartidaDTO partidaDTO){
        return iPartidaService.crearPartida(partidaDTO);
    }
    @PutMapping("/partida")
    public PartidaDTO actualizarPartida(@RequestBody PartidaDTO partidaDTO){
        return iPartidaService.actualizarPartida(partidaDTO);
    }
    @DeleteMapping("/partida/{id}")
    void eliminarPartidar(@PathVariable Long id){
        iPartidaService.eliminarPartida(id);
    };
    @GetMapping("/partidas")
    public List<PartidaDTO> buscarPartida(){
        return iPartidaService.buscarPartida();
    }
    @GetMapping("/partida/id/{id}")
    public PartidaDTO buscarPartidaPorId(@PathVariable Long id){
        return iPartidaService.buscarPartidaPorId(id);
    }
}
