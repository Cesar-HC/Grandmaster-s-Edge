package com.upc.ajedrezbackend.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;
    private Object errors;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.errors = null;
    }
}
