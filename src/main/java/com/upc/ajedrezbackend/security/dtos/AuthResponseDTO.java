package com.upc.ajedrezbackend.security.dtos;

import java.util.Set;

@lombok.Data
public class AuthResponseDTO {
    private String jwt;
    private Set<String> roles;
}
