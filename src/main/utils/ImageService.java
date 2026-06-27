package main.utils;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.UUID;

import javax.imageio.ImageIO;

import main.PropertiesManager;

public class ImageService {
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

    public static Image download(String filename) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(props.getProperty("download_url") + filename))
            .header("Accept", "image/*")
            .GET()
            .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new IOException("Download failed: " + response.statusCode());
        }

        byte[] body = response.body();
        String contentType = response.headers().firstValue("Content-Type").orElse("unknown");
        String contentEncoding = response.headers().firstValue("Content-Encoding").orElse("none");

        InputStream rawStream = new java.io.ByteArrayInputStream(body);
        InputStream imageStream = "gzip".equalsIgnoreCase(contentEncoding)
            ? new java.util.zip.GZIPInputStream(rawStream)
            : rawStream;

        Image image = ImageIO.read(imageStream);
        if (image == null) {
            throw new IOException("Could not decode image: " + filename
                + " (content-type=" + contentType + ", encoding=" + contentEncoding
                + ", bytes=" + body.length + ")");
        }

        return image;
    }
}