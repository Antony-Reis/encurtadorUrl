package com.antony.encurtador.metrics.utils;

import com.antony.encurtador.metrics.MetricsEntity;

import java.time.LocalDateTime;

public record RMetricsResponseDto(LocalDateTime acessadaQuando) {
    public RMetricsResponseDto(MetricsEntity metrics) {
        this(metrics.getAcessadaQuando());
    }
}
