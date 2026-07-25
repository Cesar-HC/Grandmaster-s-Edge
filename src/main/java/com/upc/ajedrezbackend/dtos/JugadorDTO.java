package com.upc.ajedrezbackend.dtos;

import com.upc.ajedrezbackend.security.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JugadorDTO {
    private Long id;
    private String nombreCompleto;
    private String apellidoCompleto;
    private String correoElectronico;
    private LocalDate fechaNacimiento;
    private String contrasena;
    private Double elo;
    private Long nroVictorias;
    private Long nroDerrotas;
    private Long nroEmpates;
    private String estado;
    private Role role;
}
