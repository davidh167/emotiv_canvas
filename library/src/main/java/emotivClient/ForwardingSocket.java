package emotivClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author David Hernandez
 *
 * This class extends Emotiv Socket, featuring a modified onMessage method.
 * onMessage here, forwards the data to the MQTT-Broker
 */

public class ForwardingSocket extends EmotivSocket {

    private int messageCount = 0;
    List<String[]> temp  = new ArrayList<>();
    String fileSuffix = String.valueOf(LocalDateTime.now()).replaceAll(":", "-").replaceAll("\\.", "_");


    public ForwardingSocket(URI serverURI, EmotivDelegate delegate) throws Exception {
        super(serverURI, delegate);
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

            ArrayList<String> temp2 = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                if (array.get(i) instanceof Boolean) {
                    if ((Boolean) array.get(i)) {
                        temp2.add("true");
                    } else {
                        temp2.add("false");
                    }
                } else if (array.get(i) instanceof BigDecimal) {
                    BigDecimal bd = (BigDecimal) array.get(i);
                    temp2.add(bd.toString());
                } else {
                    // Safely handle all other object types
                    System.out.println("Type: " + array.get(i).getClass() + ", Value: " + array.get(i));
                    temp2.add(String.valueOf(array.get(i))); // Safely convert any object to a String
                }
            }


        }
    }

}
