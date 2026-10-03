package com.antony.encurtador.url;

import com.antony.encurtador.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUrlRepository extends JpaRepository<UrlEntity, Long> {
    public Boolean existsByUrlEncurtada(String url);
    public Optional<UrlEntity> findByUrlEncurtada(String url);

    public Page<UrlEntity> findByUsers(UserEntity user, Pageable pageable);
    public short countByUsers(UserEntity user);
}
