package com.antony.encurtador.accessed;

import com.antony.encurtador.url.RUrlEventAccessedDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UrlConsumer {
    private final IAcessedRepository iAcessedRepository;

    public UrlConsumer(IAcessedRepository iAcessedRepository) {
        this.iAcessedRepository = iAcessedRepository;
    }

    @KafkaListener(topics = "url-accessed", groupId = "url-gruop")
    public void consume(RUrlEventAccessedDto event){
        AccessedEntity accessed = new AccessedEntity(event.urlId(), event.accessedAt());
        iAcessedRepository.save(accessed);
    }
}
