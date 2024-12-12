package app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The `DataPointListener` class listens for property change events from the
 * `Blackboard` and updates the `TrackArea` with the latest data point.
 */
public class DataPointListener implements PropertyChangeListener {

    private final TrackArea trackArea;
    private final Logger log = LoggerFactory.getLogger(DataPointListener.class);


    /**
     * Constructs a `DataPointListener`.
     *
     * @param trackArea The `TrackArea` to update with new data points.
     */
    public DataPointListener(TrackArea trackArea) {
        this.trackArea = trackArea;
    }

    /**
     * Called when a property change event is fired.
     * Updates the `TrackArea` if the event is a "newPoint" event.
     *
     * @param evt The `PropertyChangeEvent` object.
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
//        log.debug(evt.getPropertyName());
//		System.out.println("EVT" + evt);

        if ("eye_tracking_points".equals(evt.getPropertyName())) {
            Object x = evt.getNewValue();
            int[] point = (int[]) x;
            log.info("Updated point: "  + point[0] + ", " + point[1]);
            trackArea.updateLatestPoint(point[0], point[1]);
        }

        if("emotional_state".equals(evt.getPropertyName())){
            log.info("Updated the emotional state");
            trackArea.changeColor(evt.getNewValue().toString());
            trackArea.repaint();
        }
    }
}