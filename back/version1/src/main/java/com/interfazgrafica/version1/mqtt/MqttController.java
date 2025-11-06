package com.interfazgrafica.version1.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mqtt")
public class MqttController {

    @Autowired
    private MqttPublisherService publisherService;

    @GetMapping("/mqtt/send")
    public String sendMessage(@RequestParam String msg) {
        publisherService.publishMessage(msg);
        return "Mensaje publicado: " + msg;
    }

}
