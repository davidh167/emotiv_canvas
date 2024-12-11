package app;

import dataTools.Blackboard;
import dataTools.Publisher;
import dataTools.TheSubscriber;
import dataTools.TheSubscriberMQTT;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

	private Publisher server;
	private TheSubscriber subscriber;
	private static final Logger logger = LoggerFactory.getLogger(Main.class);
	private String subscriberType;

	/**
	 * Constructs the main application window and initializes components.
	 */
	public Main() {
		server = new Publisher();
		setLayout(new BorderLayout());

		JPanel dropdownPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		String[] options = {"Server", "Start", "Stop"};
		JComboBox<String> dropdownMenu = new JComboBox<>(options);
		dropdownPanel.add(dropdownMenu);
		add(dropdownPanel, BorderLayout.NORTH);

		TrackArea area = new TrackArea(server, dropdownMenu, ScreenController.getInstance());
		add(area, BorderLayout.CENTER);

		CanvasController c = new CanvasController(area, server, dropdownMenu);
		dropdownMenu.addActionListener(c);

		ScreenController.getInstance().setDrawingState("Updated TrackArea");

		Blackboard destination = Blackboard.getInstance();

		// Default for now
		subscriberType = "tcp";
		

	}

	/**
	 * Main method to launch the application.
	 *
	 * @param args Command line arguments.
	 */
	public static void main(String[] args) {
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
			logger.info("TheSubscriber stopped.");
		}
	}
}