package org.example;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;


public class App {

    public static void main(String[] args) throws URISyntaxException {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert server IP-Adress!");
        String ipAdress = scanner.nextLine();
        System.out.println("Insert server PortNo!");
        String portNo = scanner.nextLine();

        MessageHandler messageHandler = new MessageHandler();
        WebSocketClient client = new WebsocketConnection(new URI("ws://"+ipAdress+":"+portNo), messageHandler);
        messageHandler.setWebSocket(client);
        client.connect();




    }
}
