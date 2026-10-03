package com.antony.encurtador.url;

import com.antony.encurtador.url.utils.EUrlStatusType;
import com.antony.encurtador.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.apache.catalina.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_original")
    private String urlOriginal;

    @Column(name = "url_encurtada", unique = true)
    @NotNull
    private String urlEncurtada;

    @Column(name = "status")
    @NotNull
    private EUrlStatusType status;

    @CreationTimestamp
    @Column(name = "criada_quando", updatable = false)
    private LocalDateTime criadaQuando;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = true)
    private UserEntity users;

    public UrlEntity() {
    }

    public UrlEntity(String urlOriginal, String urlEncurtada, EUrlStatusType status, UserEntity users) {
        this.urlOriginal = urlOriginal;
        this.urlEncurtada = urlEncurtada;
        this.status = status;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public String getUrlOriginal() {
        return urlOriginal;
    }

    public void setUrlOriginal(String urlOriginal) {
        this.urlOriginal = urlOriginal;
    }

    public String getUrlEncurtada() {
        return urlEncurtada;
    }

    public void setUrlEncurtada(String urlEncurtada) {
        this.urlEncurtada = urlEncurtada;
    }

    public EUrlStatusType getStatus() {
        return status;
    }

    public void setStatus(EUrlStatusType status) {
        this.status = status;
    }

    public LocalDateTime getCriadaQuando() {
        return criadaQuando;
    }

    public void setCriadaQuando(LocalDateTime criadaQuando) {
        this.criadaQuando = criadaQuando;
    }

    public UserEntity getUsers() {
        return users;
    }
}