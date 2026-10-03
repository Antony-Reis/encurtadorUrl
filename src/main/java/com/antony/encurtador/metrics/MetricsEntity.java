package com.antony.encurtador.metrics;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "metrics")
public class MetricsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "acessada_quando", nullable = false)
    private LocalDateTime acessadaQuando;

    @Column(name = "url_id", nullable = false)
    private long urlId;

    public MetricsEntity() {
    }

    public MetricsEntity(long urlId, LocalDateTime acessadaQuando) {
        this.urlId = urlId;
        this.acessadaQuando = acessadaQuando;
    }

    public Long getId() {
        return id;
    }

    public long getUrlId() {
        return urlId;
    }

    public void setUrlId(long urlId) {
        this.urlId = urlId;
    }

    public LocalDateTime getAcessadaQuando() {
        return acessadaQuando;
    }

    public void setAcessadaQuando(LocalDateTime acessadaQuando) {
        this.acessadaQuando = acessadaQuando;
    }
}
