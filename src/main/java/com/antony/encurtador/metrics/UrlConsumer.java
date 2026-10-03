package com.antony.encurtador.metrics;

import com.antony.encurtador.url.utils.RUrlEventAccessedDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UrlConsumer {
    private final IMetricsRepository iAcessedRepository;

    public UrlConsumer(IMetricsRepository iAcessedRepository) {
        this.iAcessedRepository = iAcessedRepository;
    }

    @KafkaListener(topics = "url-accessed", groupId = "url-gruop")
    public void consume(RUrlEventAccessedDto event){
        MetricsEntity accessed = new MetricsEntity(event.urlId(), event.accessedAt());
        iAcessedRepository.save(accessed);
    }
}
