package com.antony.encurtador.metrics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMetricsRepository extends JpaRepository<MetricsEntity, Long> {
    Page<MetricsEntity> findByUrlId(long urlId, Pageable pageable);
    long countByUrlId(long urlId);

    void deleteAllByUrlId(Long id);
}
