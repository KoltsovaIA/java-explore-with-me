package ru.practicum.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.client.StatClient;

@Configuration
public class BeanStatClientConfig {
    private final String serverUrl;
    private final String appName;

    public BeanStatClientConfig(@Value("${statistics.server.address}") String serverUrl,
                                @Value("${application.name}") String appName) {
        this.serverUrl = serverUrl;
        this.appName = appName;
    }

    @Bean
    public StatClient createStatisticClient() {
        return new StatClient(serverUrl, appName);
    }
}
