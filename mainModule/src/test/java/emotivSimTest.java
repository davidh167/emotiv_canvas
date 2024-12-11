import dataTools.ThePublisherMQTT;
import emotivClient.EmotivSim;
import org.eclipse.paho.client.mqttv3.MqttException;

public class emotivSimTest {
    // Main method for testing
    public static void main(String[] args) throws MqttException {
        String fileName = "filtered_output.csv"; // File located in src/main/resources
        ThePublisherMQTT publisher = new ThePublisherMQTT("tcp://test.mosquitto.org:1883", "emotiv508"); // Replace with actual MQTT client
        EmotivSim sim = new EmotivSim(fileName, publisher);

        // Run simulation in a separate thread
        Thread simThread = new Thread(sim);
        simThread.start();
    }
}
