package com.antony.encurtador.exceptions;

public class UrlExpiredErrorException extends RuntimeException{
    public UrlExpiredErrorException(){
        super("Url expirada!");
    }
}
