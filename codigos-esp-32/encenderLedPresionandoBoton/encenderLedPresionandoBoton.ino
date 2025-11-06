// Pines
int pinBoton = 4;   // Pin del botón
int pinLed   = 2;   // LED integrado (GPIO2)

// Variables
int estadoBoton = HIGH;      // Estado actual del botón
int ultimoEstadoBoton = HIGH; // Último estado leído
int estadoLed = LOW;         // Estado del LED

void setup() {
  Serial.begin(115200);            // Inicia comunicación serie
  pinMode(pinBoton, INPUT_PULLUP); // Botón con resistencia interna pull-up
  pinMode(pinLed, OUTPUT);         // LED como salida
  digitalWrite(pinLed, estadoLed); // Asegura que LED arranque apagado
}

void loop() {
  // Leer botón
  estadoBoton = digitalRead(pinBoton);

  // Detectar flanco de bajada (cuando pasa de NO presionado a PRESIONADO)
  if (estadoBoton == LOW && ultimoEstadoBoton == HIGH) {
    // Cambiar estado del LED (toggle)
    estadoLed = !estadoLed;
    digitalWrite(pinLed, estadoLed);

    if (estadoLed == HIGH) {
      Serial.println("LED ENCENDIDO");
    } else {
      Serial.println("LED APAGADO");
    }

    delay(200); // Anti-rebote
  }

  // Guardar el estado del botón para la próxima lectura
  ultimoEstadoBoton = estadoBoton;
}

