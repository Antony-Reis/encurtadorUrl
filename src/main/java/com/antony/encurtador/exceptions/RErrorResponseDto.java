package com.antony.encurtador.exceptions;

import org.springframework.http.HttpStatus;

public record RErrorResponseDto(HttpStatus status, String message) {
}
