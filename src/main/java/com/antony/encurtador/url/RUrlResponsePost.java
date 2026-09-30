package com.antony.encurtador.url;

import org.springframework.http.HttpStatus;

public record RUrlResponsePost(HttpStatus status, String urlEncurtada) {
}
