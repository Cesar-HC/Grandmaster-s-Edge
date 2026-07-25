package com.upc.ajedrezbackend.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FichasDTO {
    private Long id;
    private String nombre;
    private String estado;
    private Long posicionX;
    private Long posicionY;
}
