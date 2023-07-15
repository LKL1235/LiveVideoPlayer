package io.livevideo.server.utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MyHttpClient {

    @Value("${var.filePath}")
    private String filePath;

    public String fileDownLoad(String URL) {

        String targetFilePath =filePath + URL.substring(URL.lastIndexOf("/"));;

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .GET()
                .build();

        try {
            HttpResponse<Path> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofFile(Path.of(targetFilePath)));

            if (httpResponse.statusCode() == 200) {
                Path downloadedFile = httpResponse.body();
                System.out.println("文件下载完成: " + downloadedFile);
                return targetFilePath;
            } else {
                System.out.println("文件下载失败");
                return "";
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return "";
        }
    }
}
