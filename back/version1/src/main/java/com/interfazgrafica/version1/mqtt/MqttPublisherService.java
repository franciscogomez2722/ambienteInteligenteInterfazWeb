package com.interfazgrafica.version1.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisherService {

    private final String brokerUrl = "tcp://localhost:1883";
    private final String clientId = "spring-mqtt-producer";
    private final String topic = "mensajes/test";
    private MqttClient client;

    public MqttPublisherService() {
        try {
            client = new MqttClient(brokerUrl, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            client.publish(topic, mqttMessage);
            System.out.println("Publicado desde Spring: " + message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
