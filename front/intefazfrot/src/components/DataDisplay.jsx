import React, { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const DataDisplay = () => {
  const [value, setValue] = useState("Esperando un ticket..."); // Estado para almacenar el valor recibido

  useEffect(() => {
    const socket = new SockJS("http://localhost:8080/ws"); // Usa SockJS
    const client = new Client({
      webSocketFactory: () => socket, // Usa SockJS como fábrica de WebSocket
      onConnect: () => {
        console.log("Conexión establecida con el WebSocket");
        // Suscribirse al tema "/topic/personlocation"
        client.subscribe("/topic/personlocation", (message) => {
          const newValue = JSON.parse(message.body); // Convertir el mensaje a JSON
          console.log("Datos recibidos:", newValue); // Verifica los datos recibidos

          // Actualizar el estado con los datos recibidos
          if (newValue.location && newValue.idSensor) {
            setValue(` ${newValue.location}, id_sensor: ${newValue.idSensor}`);
          } else {
            setValue("Datos incompletos recibidos");
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

    // Manejo de errores en el socket
    socket.onerror = (error) => {
      console.error("Error en el WebSocket:", error);
    };

    // Activar el cliente
    client.activate();

    // Limpiar la conexión al desmontar el componente
    return () => {
      client.deactivate();
    };
  }, []);

  return (
    <div>
      
      <h1>&nbsp;&nbsp;&nbsp;Tarea asignada: {value}</h1>
    </div>
  );
};

export default DataDisplay;