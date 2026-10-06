package com.example.tren.exception;

public class TrenNoEncontradoException extends RuntimeException {

    public TrenNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}