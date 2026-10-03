package com.antony.encurtador.exceptions;

public class ConflictErrorException extends RuntimeException{
    public ConflictErrorException(String element){
        super("Erro de conflito do elemento:" + element);
    }
}
