package net.senmori.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class Hastebin {
    private static final String HASTEBIN_URL = "https://hastebin.com/documents";

    public String postClipboard(StringSelection selection) {
        HttpClient httpsClient = HttpUtil.createHttpsClient();
        HttpRequest request = HttpUtil.createHttpRequest(URI.create(HASTEBIN_URL), selection.toString());
        JsonElement responseJson = sendHttpRequest(httpsClient, request);
        return convertAndExtractBodyAsString(responseJson, "key");
    }

    private JsonElement sendHttpRequest(HttpClient httpClient, HttpRequest request) {
        Optional<JsonElement> responseJson = Optional.of(new JsonObject());
        try {
            HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            JsonElement responseBodyAsJson = convertHttpResponseToJson(httpResponse);
            responseJson = Optional.ofNullable(responseBodyAsJson);
        } catch ( InterruptedException | IOException e ) {
            e.printStackTrace();
        }
        return responseJson.orElse(new JsonPrimitive(""));
    }

    private JsonElement convertHttpResponseToJson(HttpResponse<String> response) {
        return JsonParser.parseString(response.body());
    }

    private String convertAndExtractBodyAsString(JsonElement element, String jsonKey) {
        JsonObject jsonObject = element.getAsJsonObject();
        JsonElement jsonResponse = jsonObject.get(jsonKey);
        return jsonResponse.getAsString();
    }
}
