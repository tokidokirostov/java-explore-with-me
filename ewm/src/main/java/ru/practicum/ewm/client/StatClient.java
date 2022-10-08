package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
public class StatClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public StatClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        //public StatClient(@Value("http://stats-server:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createHit(EndpointHit endpointHit) {
        return post("/hit", endpointHit);
    }
}
