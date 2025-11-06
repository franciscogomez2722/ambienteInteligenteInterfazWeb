#include <WiFi.h>
#include <HTTPClient.h>

// Configuración Wi-Fi
const char* ssid     = "Booker DeWitt";
const char* password = "sujeto00delta";

// Pines
int pinBoton = 4;
int pinLed   = 2;

// Variables
int estadoBoton = HIGH;
int ultimoEstadoBoton = HIGH;
int estadoLed = LOW;

// URL del endpoint
String url = "http://192.168.1.192:8080/api/ruido?valor=90";

void setup() {
  Serial.begin(115200);

  pinMode(pinBoton, INPUT_PULLUP);
  pinMode(pinLed, OUTPUT);
  digitalWrite(pinLed, estadoLed);

  // Conectar Wi-Fi
  WiFi.begin(ssid, password);
  Serial.print("Conectando a Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.println("Conectado a Wi-Fi!");
  Serial.print("IP del ESP32: ");
  Serial.println(WiFi.localIP());
}

void loop() {
  estadoBoton = digitalRead(pinBoton);

  // Detectar flanco de bajada (toggle)
  if (estadoBoton == LOW && ultimoEstadoBoton == HIGH) {
    // Cambiar estado del LED
    estadoLed = !estadoLed;
    digitalWrite(pinLed, estadoLed);

    if (estadoLed == HIGH) {
      Serial.println("LED ENCENDIDO");

      // Hacer petición HTTP GET
      if(WiFi.status() == WL_CONNECTED){
        HTTPClient http;
        http.begin(url);
        int httpResponseCode = http.GET();
        if(httpResponseCode > 0){
          Serial.print("HTTP Response code: ");
          Serial.println(httpResponseCode);
        } else {
          Serial.print("Error en HTTP request: ");
          Serial.println(httpResponseCode);
        }
        http.end();
      } else {
        Serial.println("Wi-Fi desconectado");
      }

    } else {
      Serial.println("LED APAGADO");
    }

    delay(200); 
  }

  ultimoEstadoBoton = estadoBoton;
}


