package dataTools;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The `ThePublisherMQTT` class implements an MQTT publisher that connects to an MQTT
 * broker and publishes messages to specified topics.
 *
 * @author Ashton
 * @author David H.
 * @author Anthony C.
 * @version 1.0
 */
public class ThePublisherMQTT {

    private final Logger log = LoggerFactory.getLogger(ThePublisherMQTT.class.getName());
    private final MqttClient client;

    /**
     * Constructs a `ThePublisherMQTT` object and connects to the specified MQTT broker.
     *
     * @param broker The address of the MQTT broker.
     * @param clientID The unique ID of the MQTT client.
     * @throws MqttException If an error occurs during connection.
     */
    public ThePublisherMQTT(String broker, String clientID) throws MqttException {
        try {
            client = new MqttClient(broker, clientID);
            client.connect();
            log.info("Connected to broker: " + broker);
        } catch (MqttException e) {
            log.error("Unable to connect to broker: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Publishes a message to the specified topic with the given Quality of Service (QoS) level.
     *
     * @param topic The topic to publish the message to.
     * @param message The message to publish.
     * @param qos The Quality of Service level (0, 1, or 2).
     * @throws MqttException If an error occurs during publishing.
     */
    public void publish(String topic, String message, int qos) throws MqttException {
        try {
            if (!client.isConnected()) {
                log.warn("MQTT client is not connected. Reconnecting...");
                client.connect();
            }
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(qos);
            client.publish(topic, mqttMessage);
            log.info("Message published to topic: " + topic);
        } catch (MqttException e) {
            log.error("Error while publishing message: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Publishes a message to the specified topic with the default Quality of Service (QoS) level of 0.
     *
     * @param topic The topic to publish the message to.
     * @param message The message to publish.
     * @throws MqttException If an error occurs during publishing.
     */
    public void publish(String topic, String message) throws MqttException {
        publish(topic, message, 0);
    }

    /**
     * Disconnects the MQTT client from the broker and cleans up resources.
     */
    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
                log.info("MQTT client disconnected successfully.");
            }
        } catch (MqttException e) {
            log.warn("Error while disconnecting MQTT client: " + e.getMessage());
        } finally {
            try {
                if (client != null) {
                    client.close();
                    log.info("MQTT client closed successfully.");
                }
            } catch (MqttException e) {
                log.warn("Error while closing MQTT client: " + e.getMessage());
            }
        }
    }

    /**
     * Checks whether the client is connected to the MQTT broker.
     *
     * @return `true` if the client is connected, `false` otherwise.
     */
    public boolean isConnected() {
        return client.isConnected();
    }
}