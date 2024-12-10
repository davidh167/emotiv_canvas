package app;

import dataTools.Publisher;
import dataTools.Blackboard;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;


/**
 * The `TrackArea` class is a visual component that displays a face with eyes that
 * track the mouse cursor. It receives eye tracking data and visualizes it by
 * moving the pupils within the eyes. It also interacts with a `Blackboard`
 * object to display status information.
 *
 * @author Ashton
 * @author David H.
 * @author Anthony C.
 * @version 1.0
 */
public class TrackArea extends JPanel implements PropertyChangeListener {

	private ArrayList<Point> points = new ArrayList<>();
	private int latestX, latestY;
	private String drawingState;
	private static Color color = new Color(0,0,0);


	/**
	 * Constructs a `TrackArea` object.
	 *
	 * @param server The server managing the WebSocket connection.
	 * @param menu The dropdown menu for controlling the simulation.
	 * @param screen_controller The `ScreenController` object for displaying status information.
	 */
	public TrackArea(Publisher server, JComboBox<String> menu, ScreenController screen_controller) {
		setSize(800, 500);
		setVisible(true);
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

		CanvasController c = new CanvasController(this, server, menu);
		addMouseMotionListener(c);
		Border blackLine = BorderFactory.createLineBorder(Color.BLACK, 5);
		setBorder(blackLine);

		// Register as a listener for property changes from the Blackboard
		screen_controller.addPropertyChangeListener(this);
		this.drawingState = screen_controller.getDrawingState();

		Blackboard.getInstance().addPropertyChangeListener(new DataPointListener(this));
//		Blackboard.getInstance().addPropertyChangeListener(new EmotivListener(this));
	}

	public void draw(int x, int y){
		points.add(new Point(x,y));

	}

	public static void changeColor(String emotion){
		switch (emotion){
			case "Happy":
				color = Color.GREEN;
				break;
			case "Bored":
				color = Color.YELLOW;
				break;
			case "Angry":
				color = Color.RED;
				break;
			case "Excited":
				color = Color.BLUE;
				break;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(5));

		g2d.setColor(color);

		for (int i = 1; i < points.size(); i++) {
			Point p1 = points.get(i - 1);
			Point p2 = points.get(i);
			g.drawLine(p1.x, p1.y, p2.x, p2.y);
		}
		// Display latest point and drawing state
		g.setColor(Color.BLACK);
		g.drawString("Latest Point: (" + latestX + ", " + latestY + ")", 50, 50);
		g.drawString("Drawing State: " + drawingState, 50, 70);
	}

	/**
	 * Updates the latest point coordinates when a new point is received.
	 *
	 * @param x The x-coordinate of the new point.
	 * @param y The y-coordinate of the new point.
	 */
	public void updateLatestPoint(int x, int y) {
		latestX = x;
		latestY = y;
		repaint();
	}

	/**
	 * Handles property change events from the `Blackboard`.
	 *
	 * @param evt The `PropertyChangeEvent` containing the property name and new value.
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("drawingState".equals(evt.getPropertyName())) {
			this.drawingState = (String) evt.getNewValue();
			repaint();
		}
	}
}
