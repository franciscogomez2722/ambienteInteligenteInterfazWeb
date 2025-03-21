package com.interfazgrafica.version1.services;

import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FirestoreListenerService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // Para enviar mensajes a través de WebSocket

    @Autowired
    private FirebaseApp firebaseApp; // Inyectamos FirebaseApp

    @PostConstruct
    public void listenForChanges() {
        // Verificamos que FirebaseApp esté inicializado
        if (firebaseApp == null) {
            throw new IllegalStateException("FirebaseApp no ha sido inicializado.");
        }

        Firestore db = FirestoreClient.getFirestore(); // Obtiene la instancia de Firestore

        // Escucha cambios en la colección "users"
        listenForUserChanges(db);

        // Escucha cambios en la colección "locationPerson"
        listenForLocationPersonChanges(db);

        // Escucha cambios en la colección "sensorhome"
        listenForSensorChanges(db);
    }

    private void listenForUserChanges(Firestore db) {
        CollectionReference usersCollection = db.collection("users");

        usersCollection.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                System.err.println("Error al escuchar cambios en 'users': " + e.getMessage());
                return;
            }

            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    // Imprime los datos del nuevo documento en la consola
                    System.out.println("Nuevo usuario agregado:");
                    System.out.println("ID: " + dc.getDocument().getId());
                    System.out.println("Datos: " + dc.getDocument().getData());
                    System.out.println("-----------------------------");

                    // Envía los datos al frontend a través de WebSocket
                    messagingTemplate.convertAndSend("/topic/data", dc.getDocument().getData());

                    System.out.println("Datos de usuario enviados");
                }
            }
        });
    }

    private void listenForLocationPersonChanges(Firestore db) {
        CollectionReference locationPersonCollection = db.collection("locationofperson");

        locationPersonCollection.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                System.err.println("Error al escuchar cambios en 'locationPerson': " + e.getMessage());
                return;
            }

            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    // Imprime los datos del nuevo documento en la consola
                    System.out.println("Nueva ubicación agregada:");
                    System.out.println("ID: " + dc.getDocument().getId());
                    System.out.println("Datos: " + dc.getDocument().getData());
                    System.out.println("-----------------------------");

                    // Envía los datos al frontend a través de WebSocket
                    messagingTemplate.convertAndSend("/topic/personlocation", dc.getDocument().getData());

                    System.out.println("Datos de ubicación enviados");
                }
            }
        });
    }

    private void listenForSensorChanges(Firestore db) {
        CollectionReference sensorCollection = db.collection("sensorshouse1");

        sensorCollection.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                System.err.println("Error al escuchar cambios en 'sensorhome': " + e.getMessage());
                return;
            }

            // Lista para almacenar los sensores actualizados
            List<Map<String, Object>> sensorList = new ArrayList<>();

            // Recorre todos los documentos en la colección "sensorhome"
            for (DocumentSnapshot document : snapshots.getDocuments()) {
                Map<String, Object> sensorData = new HashMap<>();
                sensorData.put("idSensor", document.get("idSensor")); // Usar el ID del documento como idSensor
                sensorData.put("estado", document.get("estado")); // Obtener el campo "estado"
                sensorList.add(sensorData);
            }

            // Envía la lista de sensores al frontend a través de WebSocket
            messagingTemplate.convertAndSend("/topic/sensorhome", sensorList);

            System.out.println("Lista de sensores enviada:");
            System.out.println(sensorList);
        });
    }
}