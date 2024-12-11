package emotivClient;


import dataTools.ThePublisherMQTT;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Main class to simulate Emotiv data reading and publishing
public class EmotivSim implements Runnable {
    private String csvFilePath;
    private ThePublisherMQTT mqttPublisher;
    private final Logger logger = LoggerFactory.getLogger(EmotivSim.class);


    // Constructor
    public EmotivSim(String csvFilePath, ThePublisherMQTT mqttPublisher) {
        this.csvFilePath = csvFilePath;
        this.mqttPublisher = mqttPublisher;
    }

    // Method to read and process the CSV file
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            String[] headers = null;

            // Read headers
            if ((line = br.readLine()) != null) {
                headers = line.split(",");
            }

            // Process each line in the CSV file
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Map headers to values
                Map<String, String> dataMap = new HashMap<>();
                if (headers != null) {
                    for (int i = 0; i < headers.length && i < values.length; i++) {
                        dataMap.put(headers[i], values[i]);
                    }
                }

                // Publish the data as a JSON-like string
                String message = formatDataAsJson(dataMap);
                mqttPublisher.publish("emotiv/data", message);

                // Simulate real-time data streaming with 0.5-second delay
                Thread.sleep(500); // 500 ms delay
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper method to format data as a JSON-like string
    private String formatDataAsJson(Map<String, String> dataMap) {
        StringBuilder jsonBuilder = new StringBuilder("{");
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            jsonBuilder.append("\"")
                    .append(entry.getKey())
                    .append("\": \"")
                    .append(entry.getValue())
                    .append("\", ");
        }
        if (jsonBuilder.length() > 1) {
            jsonBuilder.setLength(jsonBuilder.length() - 2); // Remove trailing comma and space
        }
        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

//    // Main method for testing
    public static void main(String[] args) throws MqttException {
        String filePath = "../resources/filtered_output.csv"; // Replace with your CSV file path
        ThePublisherMQTT publisher = new ThePublisherMQTT("tcp://test.mosquitto.org:1883", "emotiv508"); // Replace with actual MQTT client
        EmotivSim sim = new EmotivSim(filePath, publisher);

        // Run simulation in a separate thread
        Thread simThread = new Thread(sim);
        simThread.start();
    }
}
