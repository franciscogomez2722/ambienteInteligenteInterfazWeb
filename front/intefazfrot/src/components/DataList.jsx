import React, { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const DataList = () => {
  const [sensors, setSensors] = useState([]); // Estado para almacenar los sensores

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws"); // Usa SockJS
    const client = new Client({
      webSocketFactory: () => socket, // Usa SockJS como fábrica de WebSocket
      onConnect: () => {
        console.log("Conexión establecida con el WebSocket");
        // Suscribirse al tema "/topic/sensorhome"
        client.subscribe("/topic/sensorhome", (message) => {
          const newSensors = JSON.parse(message.body); // Convertir el mensaje a JSON
          console.log("Mensaje recibido:", newSensors); // Imprimir el mensaje recibido

          // Validar que newSensors sea un array
          if (Array.isArray(newSensors)) {
            // Actualizar la lista de sensores
            setSensors(newSensors);
          } else {
            console.error("El mensaje recibido no es un array:", newSensors);
          }
        });
      },
      onDisconnect: () => {
        console.log("Desconectado del WebSocket");
      },
      onStompError: (error) => {
        console.error("Error en la conexión WebSocket:", error);
      },
      reconnectDelay: 5000, // Intentar reconectar cada 5 segundos
    });

    // Activar el cliente
    client.activate();

    // Limpiar la conexión al desmontar el componente
    return () => {
      client.deactivate();
    };
  }, []);

  return (
    <div>
      <h1>Lista de sensores en tiempo real</h1>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {sensors.map((sensor) => (
          <li
            key={sensor.idSensor} // Usar idSensor como clave única
            style={{
              marginBottom: "10px",
              padding: "10px",
              border: "1px solid #ccc",
              borderRadius: "5px",
              backgroundColor: "#f9f9f9",
            }}
          >
            <strong>idSensor:</strong> {sensor.idSensor} <br />
            <strong>Estado:</strong>{" "}
            <span
              style={{
                color: sensor.estado === "activo" ? "green" : "red",
                fontWeight: "bold",
              }}
            >
              {sensor.estado}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default DataList;