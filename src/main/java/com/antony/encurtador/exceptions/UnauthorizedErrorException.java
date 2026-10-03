package com.antony.encurtador.exceptions;
import org.springframework.security.core.AuthenticationException;

public class UnauthorizedErrorException extends AuthenticationException {
    public UnauthorizedErrorException(){
        super("Erro de autenticação");
    }
}
