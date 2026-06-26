package main.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.UUID;

import main.PropertiesManager;

public class ImageUploader {
    private static PropertiesManager props = new PropertiesManager();

    public static String upload(File file) throws IOException, InterruptedException {
        String boundary = "----Boundary" + UUID.randomUUID();
        byte[] fileBytes = Files.readAllBytes(file.toPath());

        String header = "--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"image\"; filename=\"" + file.getName() + "\"\r\n" +
            "Content-Type: application/octet-stream\r\n\r\n";
        String footer = "\r\n--" + boundary + "--\r\n";

        ByteArrayOutputStream body = new ByteArrayOutputStream();
        body.write(header.getBytes());
        body.write(fileBytes);
        body.write(footer.getBytes());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(props.getProperty("upload_url")))
            .header("X-API-Token", props.getProperty("upload_token"))
            .header("Content-Type", "multipart/form-data; boundary=" + boundary)
            .POST(HttpRequest.BodyPublishers.ofByteArray(body.toByteArray()))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Upload failed: " + response.statusCode() + " " + response.body());
        }

        return response.body();
    }
}