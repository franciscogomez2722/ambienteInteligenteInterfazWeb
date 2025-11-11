package com.interfazgrafica.version1.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SensorQueryService {

    /**
     * Obtiene la lista de sensores desde Firestore en tiempo real.
     * @return Lista de sensores con name y state
     * @throws Exception si ocurre un error en Firestore
     */
    public List<Map<String, Object>> getAllSensors() throws Exception {
        Firestore db = FirestoreClient.getFirestore(); // Obtener la instancia de Firestore
        CollectionReference sensorCollection = db.collection("concrectServices"); // Cambia al nombre de tu colección

        ApiFuture<QuerySnapshot> querySnapshot = sensorCollection.get();
        List<Map<String, Object>> sensorList = new ArrayList<>();

        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            Map<String, Object> sensorData = new HashMap<>();
            sensorData.put("name", doc.get("name"));
            sensorData.put("state", doc.get("state")); // o "estado" según tu Firestore
            sensorData.put("location", doc.get("location")); // Obtener el campo "estado"
            sensorList.add(sensorData);
        }

        return sensorList;
    }

    /**
     * Obtiene un sensor específico por ID fijo.
     */
    public Map<String, Object> getSensorById(String documentId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("locationofperson").document(documentId);
        DocumentSnapshot docSnapshot = docRef.get().get();

        if (docSnapshot.exists()) {
            return docSnapshot.getData();
        } else {
            throw new Exception("Documento no encontrado con ID: " + documentId);
        }
    }


    /**
     * Actualiza el campo "interaz" (IP) del documento que coincide con la MAC proporcionada.
     * Si no existe, crea un nuevo documento con la MAC e IP dadas.
     * @param mac Dirección MAC del dispositivo
     * @param ip Nueva dirección IP a actualizar
     * @return true si se actualizó o creó correctamente
     */
    public boolean updateIpByMac(String mac, String ip) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference collection = db.collection("concrectServices");

        // Buscar documento con la MAC dada
        ApiFuture<QuerySnapshot> query = collection.whereEqualTo("mac", mac).get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        if (documents.isEmpty()) {
            // No existe, crear nuevo documento
            System.out.println("No se encontró documento con MAC: " + mac + " → Creando nuevo...");

            Map<String, Object> newDoc = new HashMap<>();
            newDoc.put("mac", mac);
            newDoc.put("interaz", ip);
            newDoc.put("name", "Sensor_x");
            newDoc.put("state", "active");
            newDoc.put("location", "main-room");
            newDoc.put("createdAt", FieldValue.serverTimestamp());

            ApiFuture<DocumentReference> addedDocRef = collection.add(newDoc);
            System.out.println("Nuevo documento creado con ID: " + addedDocRef.get().getId());

            return true;
        }

        // Existe, actualizar su IP
        DocumentReference docRef = documents.get(0).getReference();

        Map<String, Object> updates = new HashMap<>();
        updates.put("interaz", ip);
        updates.put("lastUpdated", FieldValue.serverTimestamp());

        ApiFuture<WriteResult> writeResult = docRef.update(updates);
        System.out.println("Actualización completada en: " + writeResult.get().getUpdateTime());

        return true;
    }


}
