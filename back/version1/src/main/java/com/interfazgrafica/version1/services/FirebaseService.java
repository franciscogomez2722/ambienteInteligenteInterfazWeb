package com.interfazgrafica.version1.services;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    public void saveData(String collectionName, Object data) throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore(); // Obtiene la instancia de Firestore
        db.collection(collectionName)                 // Selecciona la colección
          .add(data)                                 // Guarda los datos y genera un ID automático
          .get();                                    // Espera a que se complete la operación
    }

    /**
     * Modifica un documento existente en Firestore.
     *
     * @param collectionName Nombre de la colección
     * @param documentId     ID del documento a modificar
     * @param newData        Mapa con los campos a actualizar
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void updateData(String collectionName, String documentId, Map<String, Object> newData)
            throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        db.collection(collectionName)
          .document(documentId)
          .update(newData)  // Actualiza los campos indicados en el mapa
          .get();           // Espera a que se complete la operación
    }


    

    public Object getData(String collectionName, String documentId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection(collectionName).document(documentId).get().get().getData();
    }
}