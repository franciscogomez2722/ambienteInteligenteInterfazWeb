package com.interfazgrafica.version1.controllers;

import com.interfazgrafica.version1.services.SensorQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sensors") // Prefijo para todos los endpoints de sensores
public class SensorQueryController {

    @Autowired
    private SensorQueryService sensorQueryService;

    /**
     * Endpoint para recuperar todos los sensores
     * GET /api/sensors/all
     */
    @GetMapping("/get-all")
    public List<Map<String, Object>> getAllSensors() throws Exception {
        return sensorQueryService.getAllSensors();
    }

        /**
     * Endpoint que actualiza la IP de un dispositivo según su MAC.
     * Ejemplo de uso:
     * GET /api/sensors/actualizarEstado?mac=AA:BB:CC:DD:EE:FF&ip=192.168.1.105
     * http://100.112.146.0:8080/api/sensors/actualizarEstado?mac=24:6F:28:AA:BB:CC&ip=192.168.1.108
     */
    @GetMapping("/actualizarEstado")
    public Map<String, Object> actualizarEstado(
            @RequestParam String mac,
            @RequestParam String ip) throws Exception {

        boolean actualizado = sensorQueryService.updateIpByMac(mac, ip);

        if (actualizado) {
            return Map.of("status", "ok", "message", "IP actualizada correctamente", "mac", mac, "ip", ip);
        } else {
            return Map.of("status", "error", "message", "No se encontró ningún documento con esa MAC", "mac", mac);
        }
    }


    
}
