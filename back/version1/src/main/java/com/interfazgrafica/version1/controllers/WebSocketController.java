package com.interfazgrafica.version1.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/new-data") // Ruta para recibir mensajes
    @SendTo("/topic/data") // Ruta para enviar mensajes a los clientes
    public String sendData(String data) {
        return data; // Envía los datos a los clientes
    }

    @SendTo("/topic/respuesta") // Ruta para enviar mensajes a los clientes
    public String sendrespuesta(String data) {
        return data; // Envía los datos a los clientes
    }

    // Nueva ruta para recibir mensajes de "personlocation"
    @MessageMapping("/personlocation")
    @SendTo("/topic/personlocation") // Envía mensajes a los clientes suscritos a "/topic/personlocation"
    public String sendPersonLocation(String locationData) {
        return locationData; // Envía los datos de ubicación a los clientes
    }

    // Nueva ruta para recibir mensajes de "personlocation"
    @MessageMapping("/sensorhome")
    @SendTo("/topic/sensorhome") // Envía mensajes a los clientes suscritos a "/topic/personlocation"
    public String sendHomeSensor(String sensorsData) {
        return sensorsData; // Envía los datos de ubicación a los clientes
    }
}