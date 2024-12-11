package emotivClient;

import org.json.JSONObject;

import dataTools.Blackboard;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmotionProcessing {

    private static final Logger logger = LoggerFactory.getLogger(EmotionProcessing.class);


    public void EmotionProcessing() {
        logger.info("Emotion processing started");
    }

    public void process(String topic, MqttMessage mqttMessage){
        try {
            // Convert the MQTT message to a JSONObject (assuming the payload is a JSON string)
            String message = new String(mqttMessage.getPayload());
            JSONObject json = new JSONObject(message);

            // Extract required values from the JSON
            double engagement = json.optDouble("engagement", 0);
            double focus = json.optDouble("focus", 0);
            double excitement = json.optDouble("excitement", 0);
            double relaxation = json.optDouble("relaxation", 0);
            double stress = json.optDouble("stress", 0);
            double interest = json.optDouble("interest", 0);

            // Calculate emotion based on the extracted values
            String emotion = determineEmotion(engagement, focus, excitement, relaxation, stress, interest);

            Blackboard.getInstance().addEmotionalState(emotion);

//            // Optionally, forward the emotion or log it
//            System.out.println("Detected Emotion: " + emotion);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Method to determine the emotion based on the extracted values
    private String determineEmotion(double engagement, double focus, double excitement, double relaxation, double stress, double interest) {
        // Weigh factors and apply logic to determine emotion
        if (engagement > 0.5 && excitement > 0.5 && interest > 0.5) {
            return "excited";
        } else if (stress > 0.5) {
            return "angry";
        } else if (relaxation > 0.5 && engagement < 0.5) {
            return "happy";
        } else {
            return "bored";
        }
    }

}
