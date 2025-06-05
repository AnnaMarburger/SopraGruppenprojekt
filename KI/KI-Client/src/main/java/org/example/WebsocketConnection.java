package org.example;

import java.net.URI;
import java.nio.ByteBuffer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

public class WebsocketConnection extends WebSocketClient{

    private MessageHandler messageHandler;

    public WebsocketConnection(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public WebsocketConnection(URI serverURI, MessageHandler messageHandler) {
        super(serverURI);
        this.messageHandler = messageHandler;
    }

    /**
     * sends join message instantly after joining
     * @param handshakedata
     */
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        send("{\n" +
                " \"type\": \"JOIN\",\n" +
                " \"version\": \"1.0\",\n" +
                " \"clientName\": \"Rudi Mentär\",\n" +
                " \"isActive\": true,\n" +
                " \"isCpu\": true\n" +
                " }");
        System.out.println("new connection opened");
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        //System.out.println("closed with exit code " + code + " additional info: " + reason);
    }


    /**
     * give received messages to the message handler and send the response to server (if there is an answer)
     * @param message
     */
    @Override
    public void onMessage(String message) {
        String response = messageHandler.parseMessage(message);

        if (response.equals("")){
            //System.out.println("No response");
            return;
        }
        send(response);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("received ByteBuffer");
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("an error occurred:" + ex);
    }


}
