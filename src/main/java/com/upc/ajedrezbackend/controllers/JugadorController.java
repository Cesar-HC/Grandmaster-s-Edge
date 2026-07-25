package com.upc.ajedrezbackend.controllers;

import com.upc.ajedrezbackend.dtos.JugadorDTO;
import com.upc.ajedrezbackend.interfaces.IJugadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", exposedHeaders = "Authorization")
@RequestMapping("/api")
public class JugadorController {
    @Autowired
    private IJugadorService iJugadorService;
    @PostMapping("/jugador")
    public JugadorDTO crearJugador(@RequestBody JugadorDTO jugadorDTO){
        return iJugadorService.crearJugador(jugadorDTO);
    }
    @PutMapping("/jugador")
    public JugadorDTO actualizarJugador(@RequestBody JugadorDTO jugadorDTO){
        return iJugadorService.actualizarJugador(jugadorDTO);
    }
    @DeleteMapping("/jugador/{id}")
    void eliminarJugador(@PathVariable Long id){
        iJugadorService.eliminarJugador(id);
    };
    @GetMapping("/jugadores")
    public List<JugadorDTO> buscarJugadores(){
        return iJugadorService.buscarJugadores();
    }
    @GetMapping("/jugador/id/{id}")
    public JugadorDTO buscarJugadoresPorId(@PathVariable Long id){
        return iJugadorService.buscarJugadoresPorId(id);
    }
    @GetMapping("/jugador/correo/{correo}")
    JugadorDTO buscarJugadoresPorCorreo(@PathVariable String correo){
        return iJugadorService.buscarJugadoresPorCorreo(correo);
    }
}
