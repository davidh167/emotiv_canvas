import java.net.URI;

import emotivClient.*;

/**
 * Main class to run the Emotiv WebSocket client.
 *
 *  @author David Hernandez
 *  @version 0.1
 */
public class Main {

    public static void main(String[] args) throws Exception {
        EmotivDelegate delegate = new EmotivDelegate();
        URI uri = new URI("wss://localhost:6868");
//        ForwardingSocket ws = new ForwardingSocket(uri, delegate, M);
//        ws.connect();
    }
}
