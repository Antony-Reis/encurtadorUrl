package com.antony.encurtador.exceptions;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.AuthenticationException;

public class AuthErrorException extends AuthenticationException {
   public AuthErrorException(){
       super("Não tem permissão para realizar essa ação");
   }
}
