package emotivClient;

import dataTools.ThePublisherMQTT;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Main class to simulate Emotiv data reading and publishing
public class EmotivSim implements Runnable {
    private String resourceFileName;
    private ThePublisherMQTT mqttPublisher;
    private final Logger logger = LoggerFactory.getLogger(EmotivSim.class);

    // Constructor
    public EmotivSim(String resourceFileName, ThePublisherMQTT mqttPublisher) {
        this.resourceFileName = resourceFileName;
        this.mqttPublisher = mqttPublisher;
    }

    // Method to read and process the CSV file
    @Override
    public void run() {
        // Load the CSV file from the resources folder
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceFileName);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                logger.error("File not found: {}", resourceFileName);
                return;
            }

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
        } catch (Exception e) {
            logger.error("Error processing file: ", e);
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

}