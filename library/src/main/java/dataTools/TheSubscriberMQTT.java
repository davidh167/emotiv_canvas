package dataTools;

import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import emotivClient.EmotionProcessing;

import java.util.Map;


/**
 * The `TheSubscriberMQTT` class implements an MQTT subscriber that connects to an MQTT
 * broker, subscribes to specified topics, and receives messages. It then forwards these
 * messages to a `DataDestination` for processing, adding a prefix to identify the
 * source of the data.
 *
 * @author Ashton
 * @author David H.
 * @author Anthony C.
 * @version 1.0
 */

public class TheSubscriberMQTT implements Runnable, MqttCallback {

    private final Logger log = LoggerFactory.getLogger(TheSubscriberMQTT.class.getName());
    private final Map<String, String> topicAndPrefixPairs;
    private final DataDestination dataDestination;

    private static final String MQTT_PREFIX = "MQTTE";
    private static final String PREFIX_DELIMITER = "~";
    private boolean running = true;

    private EmotionProcessing emotionProcessing;

    private MqttClient client;


    /**
     * Constructs a `TheSubscriberMQTT` object.
     *
     * @param broker The address of the MQTT broker.
     * @param clientID The unique ID of the MQTT client.
     * @param topicAndPrefixPairs A map of topics to subscribe to and their corresponding prefixes.
     * @param destination The `DataDestination` where received messages will be sent.
     * @throws MqttException If an error occurs during connection or subscription.
     */
    public TheSubscriberMQTT(String broker, String clientID, Map<String, String> topicAndPrefixPairs, DataDestination destination) throws MqttException {
        this.topicAndPrefixPairs = topicAndPrefixPairs;
        this.dataDestination = destination;
//        this
        try {
            client = new MqttClient(broker, clientID);
            client.setCallback(this);
            client.connect();
            log.info("Connected to broker: " + broker);
            for (String topic : topicAndPrefixPairs.keySet()){
                client.subscribe(topic);
                log.info("Subscribed to topic: " + topic);
            }
        } catch (MqttException e) {
            log.warn("Unable to connect to broker --" + e.getMessage());
            throw e;
        }
    }

    /**
     * Keeps the subscriber thread alive and idle while waiting for incoming messages.
     */
    @Override
    public void run() {
        try {
            //keep the thread alive and idle while waiting for new data
            while (running) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            String mqttErrorPrefixWithDelim = MQTT_PREFIX + PREFIX_DELIMITER ;
            dataDestination.alertError(mqttErrorPrefixWithDelim +
                    e.getMessage());
            log.warn("Thread was interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Called when the connection to the MQTT broker is lost.
     *
     * @param throwable The exception that caused the connection loss.
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("Connection lost: " + throwable.getMessage());
        while (!client.isConnected()) {
            try {
                client.connect();
                log.info("Reconnected to broker.");
            } catch (MqttException e) {
                log.error("Reconnection failed, retrying in 3 seconds...");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Called when a message arrives at a subscribed topic.
     *
     * @param topic The topic the message was received on.
     * @param mqttMessage The received message.
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
//        dataDestination.addSubscriberData(topicAndPrefixPairs.get(topic) +
//                PREFIX_DELIMITER + mqttMessage);



//        dataDestination.getIns



        log.debug("Message Arrived. Topic: " + topic +
                " Message: " + new String(mqttMessage.getPayload()));
    }

    /**
     * Called when a message delivery is complete.
     *
     * @param iMqttDeliveryToken The delivery token associated with the delivered message.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // Not used in this implementation
    }

    /**
     * Stops the MQTT subscriber.
     */
    public void stopSubscriber() {
        running = false;
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
}