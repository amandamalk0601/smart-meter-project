#include "Arduino.h"
#include "WiFi.h"
#include "esp_camera.h"
#include "PubSubClient.h"
#include "SD_MMC.h"
#include "SPI.h"
#include "FS.h"

File file;

#define SD_CS 15

// init wifi details
const char* ssid = "WIFI";
const char* password = "password";

// init mqtt details
const char *mqtt_broker = "broker";
const char *mqtt_username = "username";
const char *mqtt_password = "password";
const char *mqtt_topic = "smart-meter-readings";
const int mqtt_port = 1234;

// init csv file location
const char* csv_file = "/household1_10min.csv";

// init network connections
WiFiClient espClient;
PubSubClient client(espClient);

//Handles wifi connection
void connect_wifi() { 
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print("connecting...");
    delay(1000);
  }
  Serial.println("WiFi connected");
}

//Handles MQTT connection
void connect_mqtt() {
  
  client.setServer(mqtt_broker, mqtt_port);

  while (!client.connected()) {

    String client_id = "esp32cam-meter";
    client_id += String(WiFi.macAddress());
    Serial.printf("The client %s connects to the mqtt broker\n",
                  client_id.c_str());
    if (client.connect(client_id.c_str(), mqtt_username, mqtt_password)) {
      Serial.println("MQTT connected");
    } else {
      Serial.println("failed with state ");
      Serial.print(client.state());
    }
  }
  client.subscribe(mqtt_topic);
}

void read_csv() {
  // Open file for reading
   if (!SD_MMC.begin()) {
    Serial.println("Card Mount Failed");
    return;
  }
  
  file = SD_MMC.open(csv_file);
  
  if (!file) {
    Serial.println("File Open Failed");
    return;
  }
  
  // Read file line by line
  while (file.available()) {
      String line = file.readStringUntil('\n');
      
      // Check if line is not too long for MQTT packet
      if (line.length() <= 1024) {
        // Publish line to MQTT topic
        if (!client.publish(mqtt_topic, line.c_str())) {
          Serial.println("Publish failed");
        }    
      } else {
        Serial.println("Line too long for MQTT packet");
      }
  } 
  file.close();
  Serial.println("File closed");
 }

// Starts WIFI and MQTT connection and handles initialization and configuration of microcontroller
void setup() {
  Serial.begin(115200);
  connect_wifi();
  connect_mqtt();
  read_csv();
}

void loop() {
  // There is no need to loop
}
