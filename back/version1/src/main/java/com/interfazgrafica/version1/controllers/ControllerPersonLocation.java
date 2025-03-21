package com.interfazgrafica.version1.controllers;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.interfazgrafica.version1.services.FirebaseService;

@RestController
@RequestMapping("/api")
public class ControllerPersonLocation {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/sensor/{locationofperson}")
    public String saveData(
            @PathVariable String locationofperson, // Nombre que se envía en la URL
            @RequestBody Object data   // Datos a guardar (puede ser un Map, un POJO, etc.)
    ) throws Exception {
        String collectionName = "locationofperson"; // Colección configurada internamente
        firebaseService.saveData(collectionName, data); // El ID se genera automáticamente
        return "Data saved successfully for location Of Person: " + locationofperson;
    }


    @GetMapping("/updatelocationofperson/{location}")
    public String testSaveData(
            @PathVariable String location, // ubicacion que se envía en la URL
            @RequestParam String idSensor // idSensor como parámetro de la URL
    ) throws Exception {
        String collectionName = "locationofperson"; // Colección configurada internamente

        // Crear un mapa para simular el cuerpo de la solicitud
        Map<String, Object> data = new HashMap<>();
        data.put("location", location); // Guardar la ubicación en el mapa
        data.put("idSensor", idSensor); 

        firebaseService.saveData(collectionName, data); // El ID se genera automáticamente
        return "Data saved successfully for location: " + location;
    }
}