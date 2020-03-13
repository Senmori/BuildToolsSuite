package net.senmori.util;

import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public class HttpUtil {
    public static HttpClient createHttpsClient() {
        return HttpClient.newBuilder()
                         .version(HttpClient.Version.HTTP_2)
                         .authenticator(Authenticator.getDefault())
                         .followRedirects(HttpClient.Redirect.NORMAL)
                         .connectTimeout(Duration.ofSeconds(5))
                         .build();
    }

    public static HttpRequest createHttpRequest(URI uri, String body) {
        return HttpRequest.newBuilder()
                          .uri(uri)
                          .POST(HttpRequest.BodyPublishers.ofString(body))
                          .build();
    }
}
