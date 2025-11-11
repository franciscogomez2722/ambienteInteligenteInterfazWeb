package com.interfazgrafica.version1.controllers;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.interfazgrafica.version1.services.FirebaseService;
import com.interfazgrafica.version1.services.SensorQueryService;

//mvn dependency:resolve
//mvn dependency:purge-local-repository
//mvn clean install

@RestController
@RequestMapping("/api")
public class ControllerPersonLocation {

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private SensorQueryService sensorQueryService;

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
    public String updateLastLocation(
            @PathVariable String location,
            @RequestParam String idSensor
    ) throws Exception {
        String collectionName = "locationofperson"; // Tu colección
        String documentId = "9Vlhg3mReBAgkxdBtRBc"; // ID fijo del documento  <----- Super importante, sirve para actualizar la posicion en about

        // Crear el mapa con los campos a actualizar
        Map<String, Object> newData = new HashMap<>();
        newData.put("location", location);
        newData.put("idSensor", idSensor);

        // Llamar al servicio para actualizar el documento
        firebaseService.updateData(collectionName, documentId, newData);

        return "Documento actualizado correctamente con location: " + location;
    }

    /**
     * Recupera un sensor específico por ID fijo.
     * GET /api/sensors/get-fixed
     */
    @GetMapping("/get-fixed")
    public Map<String, Object> getFixedSensor() throws Exception {
        String fixedId = "9Vlhg3mReBAgkxdBtRBc"; // ID fijo del documento
        return sensorQueryService.getSensorById(fixedId);
    }


}