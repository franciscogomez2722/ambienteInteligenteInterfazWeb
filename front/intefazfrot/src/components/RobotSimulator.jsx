import React, { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const RobotSimulator = () => {
  const [robotPosition, setRobotPosition] = useState({ x: 0, y: 0 }); // Estado para la posición del robot
  const [currentLocation, setCurrentLocation] = useState("Esperando datos..."); // Estado para la ubicación actual
  const [idSensor, setidSensor] = useState("Esperando datos..."); // Estado para la ubicación actual

  // Posiciones de las habitaciones
  const targetPositions = {
    Cocina: { x: 50, y: 60 },
    Sala: { x: 170, y: 60 },
    Gimnasio: { x: 290, y: 60 },
    Comedor: { x: 110, y: 190 },
    Recamara: { x: 230, y: 190 },
  };

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws"); // Usa SockJS
    const client = new Client({
      webSocketFactory: () => socket, // Usa SockJS como fábrica de WebSocket
      onConnect: () => {
        console.log("Conexión establecida con el WebSocket");
        // Suscribirse al tema "/topic/personlocation"
        client.subscribe("/topic/personlocation", (message) => {
          const newLocation = JSON.parse(message.body); // Convertir el mensaje a JSON
          console.log("Datos recibidos:", newLocation);

          // Actualizar la posición del robot según la ubicación recibida
          if (newLocation.location && targetPositions[newLocation.location]) {
            setRobotPosition(targetPositions[newLocation.location]);
            setCurrentLocation(`Ubicación : ${newLocation.location}`);
            setidSensor(`Id_Sensor : ${newLocation.idSensor}`);
          } else {
            setCurrentLocation("Ubicación desconocida");
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
        {/* Dibujar las habitaciones */}
        <div
          style={{
            position: "absolute",
            left: "20px",
            top: "20px",
            width: "100px",
            height: "100px",
            border: "2px solid black",
          }}
        >
          Cocina
        </div>
        <div
          style={{
            position: "absolute",
            left: "140px",
            top: "20px",
            width: "100px",
            height: "100px",
            border: "2px solid black",
          }}
        >
          Sala
        </div>
        <div
          style={{
            position: "absolute",
            left: "260px",
            top: "20px",
            width: "100px",
            height: "100px",
            border: "2px solid black",
          }}
        >
          Gimnasio
        </div>
        <div
          style={{
            position: "absolute",
            left: "80px",
            top: "140px",
            width: "100px",
            height: "100px",
            border: "2px solid black",
          }}
        >
          Comedor
        </div>
        <div
          style={{
            position: "absolute",
            left: "200px",
            top: "140px",
            width: "100px",
            height: "100px",
            border: "2px solid black",
          }}
        >
          Recamara
        </div>

        {/* Imagen del robot */}
        <img
          src="robot.png" // Asegúrate de tener una imagen llamada robot.png en la carpeta public
          alt="Robot"
          style={{
            position: "absolute",
            left: `${robotPosition.x}px`,
            top: `${robotPosition.y}px`,
            width: "50px",
            height: "50px",
            transition: "left 0.5s, top 0.5s", // Animación suave
          }}
        />
      </div>
    </div>
  );
};

export default RobotSimulator;