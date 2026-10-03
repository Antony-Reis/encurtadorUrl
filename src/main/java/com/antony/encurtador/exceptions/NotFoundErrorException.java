package com.antony.encurtador.exceptions;

public class NotFoundErrorException extends RuntimeException{
    public NotFoundErrorException(String element){
        super(element +" não encontrado");
    }
}
