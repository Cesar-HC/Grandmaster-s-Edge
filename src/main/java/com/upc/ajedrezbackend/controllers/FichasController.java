package com.upc.ajedrezbackend.controllers;

import com.upc.ajedrezbackend.dtos.FichasDTO;
import com.upc.ajedrezbackend.interfaces.IFichasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", exposedHeaders = "Authorization")
@RequestMapping("/api")
public class FichasController {
    @Autowired
    private IFichasService iFichasService;
    @PostMapping("/ficha")
    public FichasDTO crearPartida(@RequestBody FichasDTO fichasDTO){
        return iFichasService.crearFichas(fichasDTO);
    }
    @PutMapping("/ficha")
    public FichasDTO actualizarPartida(@RequestBody FichasDTO fichasDTO){
        return iFichasService.actualizarFichas(fichasDTO);
    }
    @DeleteMapping("/ficha/{id}")
    void eliminarPartidar(@PathVariable Long id){
        iFichasService.eliminarFichas(id);
    };
    @GetMapping("/fichas")
    public List<FichasDTO> buscarPartida(){
        return iFichasService.buscarFichas();
    }
    @GetMapping("/ficha/id/{id}")
    public FichasDTO buscarPartidaPorId(@PathVariable Long id){
        return iFichasService.buscarFichasPorId(id);
    }
}
