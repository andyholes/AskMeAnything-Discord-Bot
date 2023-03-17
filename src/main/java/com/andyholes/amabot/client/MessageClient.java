package com.andyholes.amabot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MessageClient {

    @Value("${api_key}")
    private String apiKey;
    @Value("${org_key}")
    private String org;

    @Bean
    public String getResponse(String content) throws URISyntaxException, IOException, InterruptedException {

        ObjectMapper objectMapper = new ObjectMapper();
        RequestBody body = new RequestBody(
                "text-davinci-003",
                3000,
                0,
                content);
        String jsonBody = objectMapper.writeValueAsString(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readTree(response.body()).findParent("choices").findValue("text").asText();
    }

    public record RequestBody (String model, Integer max_tokens, Integer temperature, String prompt){}
}
