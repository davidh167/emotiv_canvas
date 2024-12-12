import dataTools.ThePublisherMQTT;
import dataTools.TheSubscriberMQTT;
import emotivClient.EmotivSim;
import org.eclipse.paho.client.mqttv3.MqttException;


/**
 * The 'mqttPubSubTest' class acts as a testing site for our publisher and subscribers.
 *
 * @author David H.
 */
public class mqttPubSubTest {
    public static void main(String[] args) throws MqttException {
        // Define broker URL, topic, and client ID
        String brokerUrl = "tcp://test.mosquitto.org:1883";
        String topic = "emotiv/data";
        String clientId = "TestSubscriber";

        String fileName = "filtered_output.csv"; // File located in src/main/resources

        // Create and initialize the publisher
        ThePublisherMQTT publisher = new ThePublisherMQTT(brokerUrl, "emotiv508"); // Replace with actual MQTT client
        EmotivSim sim = new EmotivSim(fileName, publisher);

        // Run simulation in a separate thread
        Thread simThread = new Thread(sim);
        simThread.start();

        // Create and initialize the subscriber
        TheSubscriberMQTT subscriber = new TheSubscriberMQTT(brokerUrl, clientId, topic);

        // Run the subscriber in a separate thread
        Thread subscriberThread = new Thread(subscriber);
        subscriberThread.start();

        // Keep the main thread alive for testing purposes
        try {
            System.out.println("Subscriber running. Press Ctrl+C to stop.");
            Thread.sleep(60000); // Keep alive for 1 minute
        } catch (InterruptedException e) {
            System.err.println("Test interrupted: " + e.getMessage());
            // Optionally interrupt the threads if needed
            simThread.interrupt();
            subscriberThread.interrupt();
        }

        // Graceful shutdown of threads after the test
        try {
            System.out.println("Shutting down...");
            subscriberThread.join();  // Wait for subscriber thread to finish
            simThread.join();         // Wait for simulation thread to finish
        } catch (InterruptedException e) {
            System.err.println("Error waiting for threads to finish: " + e.getMessage());
        }
    }
}