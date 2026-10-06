package com.antony.encurtador.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AuthErrorException.class)
    public ResponseEntity<RErrorResponseDto> authErrorException (AuthErrorException ex){
    RErrorResponseDto response = new RErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(NotFoundErrorException.class)
    public ResponseEntity<RErrorResponseDto> notFoundErrorException (NotFoundErrorException ex){
        RErrorResponseDto response = new RErrorResponseDto(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(UnauthorizedErrorException.class)
    public ResponseEntity<RErrorResponseDto> unauthorizedErrorException (UnauthorizedErrorException ex){
        RErrorResponseDto response = new RErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(UrlExpiredErrorException.class)
    public ResponseEntity<RErrorResponseDto> urlExpiredErrorException (UrlExpiredErrorException ex){
        RErrorResponseDto response = new RErrorResponseDto(HttpStatus.GONE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(response);
    }
    @ExceptionHandler(ConflictErrorException.class)
    public ResponseEntity<RErrorResponseDto> conflictErrorException(ConflictErrorException ex){
        RErrorResponseDto response = new RErrorResponseDto(HttpStatus.CONFLICT, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RErrorResponseDto> accessDeniedException (AccessDeniedException ex){
        RErrorResponseDto response = new RErrorResponseDto(HttpStatus.FORBIDDEN, "Acesso negado");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<RErrorResponseDto> usernameNotFoundException (UsernameNotFoundException ex){
        RErrorResponseDto response = new RErrorResponseDto(HttpStatus.NOT_FOUND, "Email não encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    @ExceptionHandler(MaxUrlPerUserErrorException.class)
    public ResponseEntity<RErrorResponseDto> maxUrlPerUserErrorException (MaxUrlPerUserErrorException ex){
        RErrorResponseDto response = new RErrorResponseDto(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
