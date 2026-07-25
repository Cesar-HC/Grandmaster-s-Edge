package com.upc.ajedrezbackend.entities;

import com.upc.ajedrezbackend.security.entities.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Jugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
}
