package test_1;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Test_1 {

    public static void main(String[] args) {
        String url = "http://192.168.3.82:8080/api/hola";

        try {
            // Crear cliente y petición GET
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .timeout(java.time.Duration.ofSeconds(3))
                    .build();

            // Enviar y recibir
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Mostrar resultado
            System.out.println("Codigo HTTP: " + response.statusCode());
            System.out.println("Respuesta del servidor:");
            System.out.println(response.body());

            if (response.statusCode() == 200) {
                System.out.println(">>> Llamada exitosa.");
            } else {
                System.out.println(">>> La llamada falló.");
            }
        } catch (Exception e) {
            System.err.println("Error al llamar al endpoint: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
