package com.antony.encurtador.exceptions;

public class MaxUrlPerUserErrorException extends RuntimeException{
    public MaxUrlPerUserErrorException(){
        super("Limite máximo de URLs atingidas");
    }
}
