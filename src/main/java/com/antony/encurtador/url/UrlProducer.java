package com.antony.encurtador.url;

import com.antony.encurtador.url.utils.RUrlEventAccessedDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UrlProducer {
    private final KafkaTemplate<String, RUrlEventAccessedDto> kafkaTemplate;

    public UrlProducer(KafkaTemplate<String, RUrlEventAccessedDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAccessedEvent(RUrlEventAccessedDto event){
        kafkaTemplate.send("url-accessed", event.urlId().toString(), event);
    }
}
