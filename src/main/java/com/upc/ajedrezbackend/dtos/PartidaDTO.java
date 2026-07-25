package com.upc.ajedrezbackend.dtos;

import com.upc.ajedrezbackend.entities.Fichas;
import com.upc.ajedrezbackend.entities.Jugador;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaDTO {
    private Long id;
    private Long nroPartida;
    private String nombreContrincante;
    private LocalDate fecha;
    private LocalTime duracion;
    private String nombreGanador;
    private Double eloGanadoOPerdido;
    private Jugador jugador;
    private Fichas fichas;
    private String fichasEliminadas;
    private String estado;
}
