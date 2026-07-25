package com.upc.ajedrezbackend.dtos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JugadorMatchDTO {
    private Long idJugador;
    private int elo;
}
