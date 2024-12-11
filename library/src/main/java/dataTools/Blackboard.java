package dataTools;

import org.json.JSONArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeSupport;

/**
 * The `Blackboard` class acts as a central hub for storing and managing eye tracking data.
 * It maintains a history of the last 10 eye positions and notifies any registered observers (like
 * the `TrackArea`) whenever a new data point is added. This allows for real-time updates of the
 * visualization.
 *
 * @author Ashton
 * @author David H.
 * @author Anthony C.
 * @version 1.0
 */
public class Blackboard extends PropertyChangeSupport implements DataDestination {

    // New data storage structure
    private Map<String, Object> dataStore = new HashMap<>();
    private static Blackboard instance;
    private static final Logger logger = LoggerFactory.getLogger(Blackboard.class);
    private int currentIndex = 0;


    public static Blackboard getInstance() {
        if (instance == null) {
            instance = new Blackboard();
        }
        return instance;
    }

    private Blackboard() {
        super(new Object());
    }

    // Generalized method to add or update data
    public <T> void addData(String key, T value) {
        Object oldValue = dataStore.put(key, value);
        firePropertyChange(key, oldValue, value);
        logger.debug("Data added/updated under key '{}': {}", key, value);
    }

    // Method to add eye tracking data (still applicable)
    public void addPoint(int[] newPoint) {
        int[][] points = getData("eye_tracking_points", int[][].class);
        if (points == null) {
            points = new int[10][2];
        }
        currentIndex = points.length % 10;
        points[currentIndex] = newPoint;
        currentIndex = (currentIndex + 1) % points.length;
        addData("eye_tracking_points", points);
    }

    // Method to add String data
    public void addEmotionalState(String emotionalState) {
        logger.info("Added new emotional state: {}", emotionalState);
        addData("emotional_state", emotionalState);
    }

    // General method to retrieve data from the blackboard
    public <T> T getData(String key, Class<T> type) {
        return type.cast(dataStore.get(key));
    }

    @Override
    public void addSubscriberData(String data) {
        // Handle received data (assumed to be in some format, e.g., "x~y" for points)
        String[] parts = data.split("~");
        if (parts.length == 2) {
            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                addPoint(new int[]{x, y});
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse data: " + data);
            }
        }
    }

    @Override
    public void alertError(String error) {
        logger.error("Received error: " + error);
    }
}