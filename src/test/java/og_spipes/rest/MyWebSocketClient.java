package og_spipes.rest;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

public class MyWebSocketClient extends WebSocketClient {

    private static final Logger LOG = LoggerFactory.getLogger(MyWebSocketClient.class);
    private final AtomicInteger onMessageCounter = new AtomicInteger();

    public MyWebSocketClient(URI uri) {
        super(uri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        LOG.info("Client connection successful");
    }

    @Override
    public void onMessage(String s) {
        LOG.info("Message received by client: " + s);
        onMessageCounter.incrementAndGet();
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        LOG.info("Client closed successfully");
    }

    @Override
    public void onError(Exception e) {
        LOG.error("Client error");
    }

    public AtomicInteger getOnMessageCounter() {
        return onMessageCounter;
    }

}
