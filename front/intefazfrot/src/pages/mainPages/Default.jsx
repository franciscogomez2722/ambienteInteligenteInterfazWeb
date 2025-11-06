import React, { useState } from 'react';
import axios from 'axios';

function HolaMundo() {
  const [mensaje, setMensaje] = useState('');

  const obtenerHola = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/hola');
      setMensaje(response.data);
    } catch (error) {
      setMensaje('Error al conectar con el servidor');
      console.error(error);
    }
  };

  return (
    <div>
      <button onClick={obtenerHola}>Obtener Hola Mundo</button>
      <p>{mensaje}</p>
    </div>
  );
}

export default HolaMundo;
