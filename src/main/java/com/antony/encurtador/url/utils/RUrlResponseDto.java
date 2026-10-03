package com.antony.encurtador.url.utils;

import org.springframework.http.HttpStatus;

public record RUrlResponseDto(HttpStatus status, String urlEncurtada) {
}
