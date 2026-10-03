package com.antony.encurtador.metrics.utils;

import com.antony.encurtador.url.UrlEntity;
import com.antony.encurtador.url.utils.EUrlStatusType;

public record
RUrlResponseDto(String urlOriginal, String urlEncurtada, EUrlStatusType status) {
    public RUrlResponseDto(UrlEntity url) {
        this(url.getUrlOriginal(), url.getUrlEncurtada(), url.getStatus());
    }
}
