package com.antony.encurtador.url;

import java.time.LocalDateTime;

public record RUrlEventAccessedDto(Long urlId, LocalDateTime accessedAt) {
}
