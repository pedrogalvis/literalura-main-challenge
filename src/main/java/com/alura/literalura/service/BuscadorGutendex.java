package com.alura.literalura.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class BuscadorGutendex {
    private static final String URL_BASE="https://gutendex.com/books?search=";
    public String obtenBusqueda(String url){
        String urlCompleto=URL_BASE+url;
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlCompleto))
                .build();
        try {
            HttpResponse<String> response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("URL: "+urlCompleto);
//            System.out.println("Response code: "+response.statusCode());

            if(response.statusCode()==200){
                return response.body();
            }else{
                throw new RuntimeException("Failed to fetch");
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
