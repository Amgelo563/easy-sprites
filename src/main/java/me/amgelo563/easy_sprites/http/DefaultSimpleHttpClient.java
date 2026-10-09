package me.amgelo563.easy_sprites.http;

import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public enum DefaultSimpleHttpClient implements SimpleHttpClient {
    INSTANCE;

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .version(HttpClient.Version.HTTP_1_1)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private static final RetryPolicy<Object> RETRY = RetryPolicy.builder()
            .handle(Exception.class)
            .withBackoff(Duration.ofMillis(200), Duration.ofSeconds(5))
            .withMaxRetries(3)
            .build();

    @Override
    public String getAsString(URI uri) {
        return get(uri, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public InputStream getAsInputStream(URI uri) {
        return get(uri, HttpResponse.BodyHandlers.ofInputStream());
    }

    private <T> T get(URI uri, HttpResponse.BodyHandler<T> handler) {
        return Failsafe.with(RETRY).get(() -> {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.ofSeconds(15))
                    .header("User-Agent", "easy-sprites-plugin/http-client")
                    .GET()
                    .build();
            HttpResponse<T> response = CLIENT.send(request, handler);

            if (response.statusCode() >= 500) throw new RuntimeException("Server returned " + response.statusCode());

            return response.body();
        });
    }
}
