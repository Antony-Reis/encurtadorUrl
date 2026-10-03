package com.antony.encurtador.config;

import com.antony.encurtador.url.utils.RUrlEventAccessedDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;


import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers[0]}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, RUrlEventAccessedDto> urlProducerFactory(){
        Map<String, Object> props = new HashMap<>();
        props.put(JacksonJsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
    @Bean
    public KafkaTemplate<String, RUrlEventAccessedDto> urlKafkaTemplate(){
        return new KafkaTemplate<>(urlProducerFactory());
    }
}
