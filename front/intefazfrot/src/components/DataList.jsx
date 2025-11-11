import React, { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import axios from "axios"; // Para hacer la consulta REST inicial

const DataList = () => {
  const [sensors, setSensors] = useState([]); // Estado para almacenar los sensores

  useEffect(() => {
    // 1- Primero: Obtener la lista de sensores desde el endpoint REST
    const fetchSensors = async () => {
      try {
        const response = await axios.get("http://100.112.146.0:8080/api/sensors/get-all");
        setSensors(response.data);
        console.log("Sensores cargados desde REST:", response.data);
      } catch (error) {
        console.error("Error al cargar sensores desde REST:", error);
      }
    };

    fetchSensors();

    // 2️- Segundo: Conectarse al WebSocket para recibir actualizaciones en tiempo real
    const socket = new SockJS("http://100.112.146.0:8080/ws");
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log("Conexión establecida con el WebSocket");
        client.subscribe("/topic/sensorhome", (message) => {
          const updatedSensors = JSON.parse(message.body);
          if (Array.isArray(updatedSensors)) {
            setSensors(updatedSensors);
            console.log("Actualización en tiempo real:", updatedSensors);
          } else {
            console.error("El mensaje recibido no es un array:", updatedSensors);
          }
        });
      },
      onDisconnect: () => {
        console.log("Desconectado del WebSocket");
      },
      onStompError: (error) => {
        console.error("Error en la conexión WebSocket:", error);
      },
      reconnectDelay: 5000,
    });

    client.activate();

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
            key={sensor.name} // o sensor.id si existe
            style={{
              marginBottom: "10px",
              padding: "10px",
              border: "1px solid #ccc",
              borderRadius: "5px",
              backgroundColor: "#f9f9f9",
            }}
          >
            <strong>Name:</strong> {sensor.name} <br />
            <strong>Location:</strong> {sensor.location} <br />
            <strong>State:</strong>{" "}
            <span
              style={{
                color: sensor.state === "active" ? "green" : "red",
                fontWeight: "bold",
              }}
            >
              {sensor.state}
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default DataList;
