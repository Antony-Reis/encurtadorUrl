package com.antony.encurtador.url;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUrlRepository extends JpaRepository<UrlEntity, Long> {
    public Boolean existsByUrlEncurtada(String url);
    public Optional<UrlEntity> findByUrlEncurtada(String url);
}
