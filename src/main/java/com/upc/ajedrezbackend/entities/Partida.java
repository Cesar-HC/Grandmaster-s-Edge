package com.upc.ajedrezbackend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Partida {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long nroPartida;
    private String nombreContrincante;
    private LocalDate fecha;
    private LocalTime duracion;
    private String nombreGanador;
    private Double eloGanadoOPerdido;
    @ManyToOne
    @JoinColumn(name="idJugador", nullable = false)
    private Jugador jugador;
    @ManyToOne
    @JoinColumn(name="idFicha", nullable = false)
    private Fichas fichas;
    private String fichasEliminadas;
    private String estado;
}
