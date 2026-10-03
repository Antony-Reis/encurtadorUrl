package com.antony.encurtador.user.utils;

import org.springframework.http.HttpStatus;

public record RUserResponseDto(HttpStatus status, String message) {
}
