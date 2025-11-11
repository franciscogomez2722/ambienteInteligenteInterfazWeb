import React, { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

const UserDisplay = () => {
  const [username, setUsername] = useState("Esperando usuario...");

  useEffect(() => {
    const socket = new SockJS("http://100.112.146.0:8080/ws");
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log("Conexión establecida con el WebSocket");

        client.subscribe("/topic/data", (message) => {
          const userData = JSON.parse(message.body);
          console.log("Datos de usuario recibidos:", userData);
          console.log(userData.name);

          // Ahora buscamos 'name' en lugar de 'username'
          if (userData.name) {
            setUsername(userData.name);
          } else {
            setUsername("Usuario desconocido");
          }
        });
      },
      onDisconnect: () => console.log("Desconectado del WebSocket"),
      onStompError: (error) => console.error("Error en la conexión WebSocket:", error),
      reconnectDelay: 5000,
    });

    client.activate();

    return () => client.deactivate();
  }, []);

  return (
    <div>
      <h1>Nuevo usuario agregado: {username}</h1>
    </div>
  );
};

export default UserDisplay;
