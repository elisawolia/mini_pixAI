package com.minipixal.webapp.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

/**
 * Универсальный маппер для объектов
 */
@Component
public class UtilObjectMapper {

    public final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.UNWRAP_ROOT_VALUE)
            .disable(SerializationFeature.WRAP_ROOT_VALUE)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());

    public String mapToString(Object request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
