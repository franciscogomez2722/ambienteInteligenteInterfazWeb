import React, { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import axios from "axios"; // Para hacer la petición HTTP

const RobotSimulator = () => {
  const [robotPosition, setRobotPosition] = useState({ x: 0, y: 0 });
  const [currentLocation, setCurrentLocation] = useState("Esperando datos...");
  const [idSensor, setidSensor] = useState("Esperando datos...");
  
  const targetPositions = {
    Cocina: { x: 50, y: 60 },
    Tocador: { x: 170, y: 60 },
    Gimnasio: { x: 290, y: 60 },
    Comedor: { x: 110, y: 190 },
    Recamara: { x: 230, y: 190 },
  };

  // Función para actualizar la posición según los datos del sensor
  const updatePositionFromData = (data) => {
    if (data.location && targetPositions[data.location]) {
      setRobotPosition(targetPositions[data.location]);
      setCurrentLocation(`Ubicación : ${data.location}`);
      setidSensor(`Id_Sensor : ${data.idSensor}`);
    } else {
      setCurrentLocation("Ubicación desconocida");
    }
  };

  useEffect(() => {
    // Recuperar información del documento con ID fijo al cargar la página
    axios
      .get("http://100.112.146.0:8080/api/get-fixed")
      .then((res) => {
        console.log("Datos iniciales del sensor:", res.data);
        updatePositionFromData(res.data);
      })
      .catch((err) => {
        console.error("Error al obtener sensor fijo:", err);
      });

    // Suscribirse al WebSocket para actualizaciones en tiempo real
    const socket = new SockJS("http://100.112.146.0:8080/ws");
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log("Conexión establecida con WebSocket");

        client.subscribe("/topic/personlocation", (message) => {
          const newLocation = JSON.parse(message.body);
          console.log("Datos recibidos en tiempo real:", newLocation);
          updatePositionFromData(newLocation);
        });
      },
      onDisconnect: () => console.log("Desconectado del WebSocket"),
      onStompError: (err) => console.error("Error WebSocket:", err),
      reconnectDelay: 5000,
    });

    client.activate();

    return () => {
      client.deactivate();
    };
  }, []);

  return (
    <div style={{ textAlign: "center", marginTop: "20px" }}>
      <h1>Simulador de Casa</h1>
      <h2>{currentLocation}</h2>
      <h2>{idSensor}</h2>
      <div
        style={{
          position: "relative",
          width: "400px",
          height: "300px",
          backgroundColor: "lightgray",
          margin: "0 auto",
          border: "2px solid black",
        }}
      >
        {/* Dibujar habitaciones */}
        {Object.entries(targetPositions).map(([name, pos], index) => (
          <div
            key={index}
            style={{
              position: "absolute",
              left: `${pos.x - 30}px`,
              top: `${pos.y - 30}px`,
              width: "100px",
              height: "100px",
              border: "2px solid black",
            }}
          >
            {name}
          </div>
        ))}

        {/* Robot */}
        <img
          src="robot.png"
          alt="Robot"
          style={{
            position: "absolute",
            left: `${robotPosition.x}px`,
            top: `${robotPosition.y}px`,
            width: "50px",
            height: "50px",
            transition: "left 0.5s, top 0.5s",
          }}
        />
      </div>
    </div>
  );
};

export default RobotSimulator;
