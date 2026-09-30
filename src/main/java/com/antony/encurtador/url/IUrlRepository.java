package com.antony.encurtador.url;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.expression.spel.ast.OpInc;

import java.util.Optional;

public interface IUrlRepository extends JpaRepository<UrlEntity, Long> {
    public Boolean existsByUrlOriginal(String url);
    public Boolean existsByUrlEncurtada(String url);
    public Optional<UrlEntity> findByUrlEncurtada(String url);
}
