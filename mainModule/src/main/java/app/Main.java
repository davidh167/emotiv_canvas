package app;

import dataTools.ThePublisherMQTT;
import dataTools.TheSubscriberMQTT;
import emotivClient.EmotivSim;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * The `Main` class is the entry point of the eye tracking simulation application. It sets up
 * the main window, initializes key components (server, subscriber, UI elements), and manages
 * their interaction.
 *
 * @author Ashton
 * @author David H.
 * @author Anthony C.
 * @version 1.0
 */
public class Main extends JFrame {

//	private TheSubscriber tcpSubscriber;
//	private Publisher server;
	private ThePublisherMQTT mqtt_pub;
	private TheSubscriberMQTT subscriber;
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private String subscriberType;
	private boolean mqttPubInstantiate;

	/**
	 * Constructs the main application window and initializes components.
	 */
	public Main() throws MqttException {

		String brokerUrl = "tcp://test.mosquitto.org:1883";
		String clientID = "myClientCSC508";
		String publishID = "emotiv508";

		String fileName = "filtered_output.csv"; // File located in src/main/resources

//		server = new Publisher();

		setLayout(new BorderLayout());

		JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		String[] options = {"Server", "Start", "Stop"};
		JComboBox<String> dropdownMenu = new JComboBox<>(options);
		dropdownPanel.add(dropdownMenu);
		add(dropdownPanel, BorderLayout.NORTH);

		TrackArea area = new TrackArea(dropdownMenu, ScreenController.getInstance());
		add(area, BorderLayout.CENTER);

		CanvasController c = new CanvasController(area, dropdownMenu);
		dropdownMenu.addActionListener(c);

		ScreenController.getInstance().setDrawingState("Updated TrackArea");

		// Defaults for now
		subscriberType = "mqtt";
		mqttPubInstantiate = true;


		// If we should start in testing mode (Emotiv data simulation)
		if (mqttPubInstantiate) {
			// Create and initialize the publisher
			mqtt_pub = new ThePublisherMQTT(brokerUrl, publishID); // Replace with actual MQTT client
			EmotivSim sim = new EmotivSim(fileName, mqtt_pub);

			// Run simulation in a separate thread
			Thread simThread = new Thread(sim);
			simThread.start();
		}


		if (subscriberType.equals("mqtt")) {
			try {
				// Instantiate the subscriber
				TheSubscriberMQTT subscriber = new TheSubscriberMQTT(brokerUrl, clientID, "emotiv/data");

				// Run the subscriber in a separate thread
				Thread subscriberThread = new Thread(subscriber);
				subscriberThread.start();
				logger.info("Subscriber initialized and started.");

			} catch (MqttException e) {
				System.out.println("An error occurred while initializing the subscriber: " + e.getMessage());
			}
		}else{
			logger.error("Unknown subscriber type");
		}

	}

	/**
	 * Main method to launch the application.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) throws MqttException {
		Main main = new Main();
		main.setTitle("Eye Tracker Simulator");
		main.setSize(800, 600);
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setVisible(true);
		logger.info("Eye Tracker Simulator application started.");
	}

	/**
	 *  Called when the application window is closed.
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (subscriber != null) {
			subscriber.stopSubscriber();
			logger.info("Subscriber stopped.");
		}
	}
}