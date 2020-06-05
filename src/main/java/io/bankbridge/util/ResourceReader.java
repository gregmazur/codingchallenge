package io.bankbridge.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public interface ResourceReader {
    static  <T> T readResources(String path, Class<T> clazz) throws IOException {
        return (T) new ObjectMapper().readValue(
                Thread.currentThread().getContextClassLoader().getResource(path), clazz);
    }
}
