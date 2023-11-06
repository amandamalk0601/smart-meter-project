# Testing Smart Meter System Design

## Overall Requirements
- Download and install Apache Flink Version [1.17.0](https://flink.apache.org/downloads/)
- Download and install Apache Kafka Version [3.4.0](https://kafka.apache.org/downloads)
- Java 11
## Prerequisites
- Adjust the following line in the `Average_Power_Consumption.java` and `Peak_Power_Consumption.java`, to set the location for the output CSV file.
```
final FileSink<String> sink = FileSink.forRowFormat(new Path("path/to/which/you/want/to/save/the/output/csv", new SimpleStringEncoder<String>())
```
- Adjust the folder path to Kafka in `restart_topics.sh`
   ```
   for topic in "${topic_names[@]}"; do
    kafka-3.4.0-src/bin/kafka-topics.sh --bootstrap-server "$bootstrap_servers" --delete --topic "$topic"
   done
   ```
## Guide to Run the Simulated Scenario of Multiple Households

### Additional Requirements
- Python 3.10
- Install the necessary packages
   ```
  pip install kafka-python pandas
   ```

## Prerequisites
- Adjust the following line in the `local_kafka_producer.py`, to set the location of the CSV files, which contain the household data.
```
folder_path = os.getcwd()+ "/10_min_data/"
```
### Steps

1. Run Apache Flink cluster
   ```
   bash flink-1.17.0/bin/start-cluster.sh
   ```
2. Run Apache Kafka environment
- Open a new terminal and run:
  ```
  bin/zookeeper-server-start.sh config/zookeeper.properties
  ```
- Open another new terminal and run:
   ```
  bin/kafka-server-start.sh config/server.properties
   ```
3. Create Kafka Topics
   ```
   bash restart_topics.sh
   ```
4. Run Local Data Producer
   ```
   python local_kafka_producer.py
   ```
5. Run the Apache Flink job of your choosing, either `Average_Power_Consumption.java` or `Peak_Power_Consumption.java`

6. After running the project, stop the Apache Flink cluster with:
   ```
   bash flink-1.17.0/bin/stop-cluster.sh
   ```

Purge the topics from Kafka (as they are not deleted automatically) and recreate the topics using:
   ```
   bash restart_topics.sh
   ```


## Guide to Run the Scenario with ESP32CAM Transmitting Data
## Prerequisites
- The ESP32CAM SD card has to contain the household data
### Additional Requirements
- Install Node.js
- Download the mqtt-to-kafka-bridge project from [here](https://github.com/nodefluent/mqtt-to-kafka-bridge)
- Go to `mqtt-to-kafka-bridge/lib/MqttClient.js` and change the line:
 ```javascript
 this.client.subscribe("#", (error, qos) => {
 ```
 to this line:
 ```javascript
 this.client.subscribe("smart-meter-readings", (error, qos) => {
 ```
 This is necessary to tell the bridge to which topic it should subscribe.

### Setup

**ESP32CAM**
- In the `MQTT.ino` file, adjust the WiFi details according to yours
- Flash the `MQTT.ino` project on the ESP32CAM
- Flashing issues: if any flashing issues occur, it could be due to the following:
  - Before connecting the ESP32CAM to the computer, make sure to keep the flash button pressed on the microcontroller. While pressing it, connect it to the computer. The microcontroller has a unique way of enabling the flashing process.
  - Missing drivers: If you run it on an Ubuntu-based system, follow the steps detailed [here](https://github.com/juliagoda/CH341SER#tutorial-on-ubuntu)

### Steps

1. Run Apache Flink cluster
   ```
   bash flink-1.17.0/bin/start-cluster.sh
   ```
2. Run the bridge and keep that terminal open
   ```
   node mqtt-to-kafka-bridge/example/sample.js
   ```
   If you want to print debug messages during redirection of the messages:
   ```
   DEBUG=mqtttokafka:bridge node mqtt-to-kafka-bridge/example/sample.js
   ```
3. Run Apache Kafka environment
- Open a new terminal and run:
  ```
  bin/zookeeper-server-start.sh config/zookeeper.properties
  ```
- Open another new terminal and run:
   ```
  bin/kafka-server-start.sh config/server.properties
   ```
4. Create Kafka Topics
   ```
   bash restart_topics.sh
   ```
5. Run the Apache Flink job of your choosing, either `Average_Power_Consumption.java` or `Peak_Power_Consumption.java`
6. Connect ESP32CAM to a power source to start the transmission
7. After running the project, stop the Apache Flink cluster with:
   ```
   bash flink-1.17.0/bin/stop-cluster.sh
   ```

Purge the topics from Kafka and recreate the topics using:
   ```
   bash restart_topics.sh
   ```

For debugging:
Print out all available topics:
  ```
  bin/kafka-topics.sh --list --bootstrap-server localhost:9092
  ```

Run a testing consumer to see if messages arrive to Kafka:
  ```
  bash kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic household1 --from-beginning
  ```

## Acknowledgments

This project makes use of the following repositories:

- [AI-on-the-edge](https://github.com/jomjol/AI-on-the-edge-device)
  - By: jomjol
  - Description: This project is not available on this repository, it was flashed onto an ESP32CAM to obtain real meter readings as part of the "Guide to Run Scenario with ESP32CAM Transmitting Data".

- [mqtt-to-kafka-bridge](https://github.com/nodefluent/mqtt-to-kafka-bridge)
  - By: nodefluent
  - License: [MIT License](https://github.com/nodefluent/mqtt-to-kafka-bridge/blob/master/LICENSE)
  - Description: This project is not available on this repository, it was used as part of the "Guide to Run Scenario with ESP32CAM Transmitting Data" for the transmission process between the ESP32CAM microcontrollers MQTT and Apache Kafka.
