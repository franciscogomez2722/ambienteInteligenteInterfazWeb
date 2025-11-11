package com.interfazgrafica.version1.controllers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Autowired;

import com.interfazgrafica.version1.services.FirebaseService;

import java.io.PrintWriter;
import java.net.Socket;

@RestController
@RequestMapping("/api2")
public class MyController {

    @Autowired
    private FirebaseService firebaseService;

    // Contador atómico para evitar condiciones de carrera en concurrencia
    private AtomicInteger contador = new AtomicInteger(0);

    // RestTemplate con timeout configurado para llamar al ESP32
    private final RestTemplate restTemplate;

    // Dirección del ESP32; idealmente esto se debería mover a application.properties o @Value
    private static final String ESP32_URL = "http://192.168.3.162/led/on";

    public MyController() {
        // Configuramos un RestTemplate con timeouts cortos para no colgar la petición si el ESP32 no responde
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(1500); // 1.5 segundos para conectar
        requestFactory.setReadTimeout(1500);    // 1.5 segundos para leer
        this.restTemplate = new RestTemplate(requestFactory);
    }

    /**
     * Guarda datos en Firebase bajo la colección "users". El nombre se recibe en la ruta y los datos en el cuerpo.
     */
    @PostMapping("/save/{name}")
    public String saveData(
            @PathVariable String name, // Nombre que se envía en la URL
            @RequestBody Object data   // Datos a guardar (puede ser un Map, un POJO, etc.)
    ) throws Exception {
        String collectionName = "users"; // Colección fija por ahora
        firebaseService.saveData(collectionName, data); // Se guarda y el ID lo genera Firebase
        return "Data saved successfully for user: " + name;
    }

    /**
     * Endpoint de prueba que arma un objeto con nombre, email y edad y lo guarda en Firebase.
     * http://100.112.146.0:8080/api2/test-save/Francisco?email=francisco@example.com&age=30
     */
    @GetMapping("/test-save/{name}")
    public String testSaveData(
            @PathVariable String name, // Nombre que se envía en la URL
            @RequestParam String email, // Email como parámetro de la URL
            @RequestParam int age // Edad como parámetro de la URL
    ) throws Exception {
        String collectionName = "users";

        // Creamos un mapa simulando un body
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("age", age);

        firebaseService.saveData(collectionName, data);
        return "Data saved successfully for user: " + name;
    }

    //Funcion para enviar datos a el midleware :D
    public void enviarMensajeAlNodo() {
        String ipNodo = "100.112.146.0"; 
        int puerto = 4567;
        try (Socket socket = new Socket(ipNodo, puerto);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("Ruido fuerte detectado en cocina");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Lista de sensores
    @GetMapping("/sensores")
    public Map<String, String> obtenerEstadoSensores() {
        Map<String, String> estadoSensores = new HashMap<>();
        estadoSensores.put("sensor1", "Activo");
        estadoSensores.put("sensor2", "Apagado");
        estadoSensores.put("sensor3", "Activo");
        return estadoSensores;
    }


    /**
     * Endpoint simple que recibe un parámetro llamado 'valor' y lo imprime en consola.
     * http://192.168.1.192:8080/api/ruido?valor=123
     * 
     */
    @GetMapping("/ruido")
    public String recibirRuido(@RequestParam String valor) {
        System.out.println("Valor recibido por el sensor de sonido: " + valor);
        enviarMensajeAlNodo();
        return "Recibido: " + valor;
    }

    /**
     * Endpoint /hola que:
     *  - Incrementa el contador.
     *  - Llama al ESP32 para encender el LED.
     *  - Devuelve en la respuesta si la llamada al ESP32 fue "exito" o "fracaso" junto con detalles.
     */
    @GetMapping("/hola")
    public Map<String, Object> sayHello() {
        int valor = contador.incrementAndGet();  // Incrementa y obtiene el valor actual
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "....");
        respuesta.put("contador", valor);

        try {
            // Realiza la llamada HTTP GET al ESP32
            ResponseEntity<Map> espResponse = restTemplate.getForEntity(new URI(ESP32_URL), Map.class);

            // Verifica que la respuesta fue 2xx y contiene cuerpo
            if (espResponse.getStatusCode().is2xxSuccessful() && espResponse.getBody() != null) {
                Object status = espResponse.getBody().get("status");
                if ("ok".equals(String.valueOf(status))) {
                    // El ESP32 respondió que todo está bien
                    respuesta.put("esp32", "exito");
                } else {
                    // El ESP32 respondió pero con un payload inesperado
                    respuesta.put("esp32", "fracaso");
                    respuesta.put("detalle_esp32", espResponse.getBody());
                }
            } else {
                // Código HTTP no exitoso
                respuesta.put("esp32", "fracaso");
                respuesta.put("detalle", "Respuesta no exitosa del ESP32: código " + espResponse.getStatusCode());
            }
        } catch (Exception e) {
            // Captura errores de conexión, timeout, URI mal formada, etc.
            respuesta.put("esp32", "fracaso");
            respuesta.put("error", e.getMessage());
        }

        return respuesta;
    }
}
