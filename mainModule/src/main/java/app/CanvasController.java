package app;

import dataTools.Blackboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;


/**
 * The `EyeController` class manages the eye tracking simulation by responding to mouse
 * movements and user actions from a dropdown menu. It updates the eye position in the
 * `TrackArea` and sends data to the `Server` for processing.
 *
 * @author Ashton
 * @author David H.
 * @author Anthony C.
 * @version 1.0
 */
public class CanvasController implements MouseMotionListener, ActionListener {

	private TrackArea trackArea;

	private JComboBox<String> menu;
	private static boolean drawingToggle = false; // Enables and disbles drawing.
	private static final Logger logger = LoggerFactory.getLogger(CanvasController.class);

	/**
	 * Constructs a `EyeController`.
	 *
	 * @param trackArea The `TrackArea` where eye movements are visualized.
	 * @param menu The dropdown menu for controlling the server (Start/Stop).
	 */
	public CanvasController(TrackArea trackArea, JComboBox<String> menu) {
		this.trackArea = trackArea;
		this.menu = menu;
		Blackboard.getInstance().addPropertyChangeListener(new DataPointListener(trackArea));
	}

	/**
	 * Updates the eye position and sends data to the `Blackboard` when the mouse is moved.
	 *
	 * @param e The mouse movement event.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		if (drawingToggle) {
			int x = e.getX();
			int y = e.getY();
			trackArea.draw(x, y);
			int[] newPoint = {x, y};
			Blackboard.getInstance().addPoint(newPoint);
			logger.info("Mouse moved to: ({}, {})", x, y);
		}
	}

	/**
	 *  Handles mouse drag events (currently not used).
	 *
	 * @param e The mouse drag event.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	/**
	 * Handles actions from the dropdown menu (e.g., starting or stopping the server).
	 *
	 * @param e The action event.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String selectedAction = (String) menu.getSelectedItem();
		if (selectedAction.equals("Start")) {
			drawingToggle = true;
			logger.info("app.library.Server started by user action.");
		} else if (selectedAction.equals("Stop")) {
			drawingToggle = false;
			logger.info("app.library.Server stopped by user action.");
		}
	}
}