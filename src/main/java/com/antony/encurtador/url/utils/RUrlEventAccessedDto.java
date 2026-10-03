package com.antony.encurtador.url.utils;

import java.time.LocalDateTime;

public record RUrlEventAccessedDto(Long urlId, LocalDateTime accessedAt) {
}
