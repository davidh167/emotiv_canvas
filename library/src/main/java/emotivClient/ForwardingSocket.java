package emotivClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dataTools.ThePublisherMQTT;


/**
 * This class extends Emotiv Socket, featuring a modified onMessage method.
 * onMessage here, forwards the data to the MQTT-Broker
 *
 *
 * @author David Hernandez
 *
 */

public class ForwardingSocket extends EmotivSocket {

    private int messageCount = 0;
    private ThePublisherMQTT mqttPublisher;
    private final Logger log = LoggerFactory.getLogger(ForwardingSocket.class.getName());
    List<String[]> temp  = new ArrayList<>();
    String fileSuffix = String.valueOf(LocalDateTime.now()).replaceAll(":", "-").replaceAll("\\.", "_");


    public ForwardingSocket(URI serverURI, EmotivDelegate delegate) throws Exception {
        super(serverURI, delegate);
        String broker = "tcp://test.mosquitto.org:1883";
        String clientID = "emotivForwardingSocket";
        this.mqttPublisher = new ThePublisherMQTT(broker, clientID);
    }


    @Override
    public void onMessage(String message) {

        System.out.println("Received message from Emotiv server.");
        System.out.println(fileSuffix);
        if (!delegate.isSubscribed()) {
            JSONObject response = new JSONObject(message);
            int id = response.getInt("id");
            System.out.println(response);
            Object result = response.get("result");
            delegate.handle (id, result, this);
        } else {

            try {
                BigDecimal time = new JSONObject(message).getBigDecimal("time");
                JSONObject object = new JSONObject(message);
                System.out.println(object);
        //            System.out.println(time);
                JSONArray array = null;
                if ((object.keySet()).contains("fac")) {
                    array = object.getJSONArray("fac");
                } else if ((object.keySet()).contains("dev")) {
                    array = object.getJSONArray("dev");
                } else if ((object.keySet()).contains("met")) {
                    array = object.getJSONArray("met");
                } else if ((object.keySet()).contains("mot")) {
                    array = object.getJSONArray("mot");
                }

                // if fac refers to facial expression, and met refers to mental state, what does dev refer to?
                System.out.println(time + " :: " + array);

                if (array != null) {
                    String payload = array.toString();
                    mqttPublisher.publish("emotions/data", payload);
                    System.out.println("Forwarded data to MQTT Broker");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
    }

}
