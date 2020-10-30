package communication;

import java.util.Observable;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.DeploymentException;

import seabattlegame.SeaBattleGame;
import seabattleshared.*;
import java.io.IOException;
import java.net.URISyntaxException;

@ClientEndpoint
public class CommunicatorClientWebSocket extends Observable {

    // Singleton
    public static CommunicatorClientWebSocket instance = null;

    /**
     * The local websocket uri to connect to.
     */
    public final String uri = "ws://localhost:8095/communicator/";

    public Session session;

    public String message;

    public Gson gson = null;

    // Status of the webSocket client
    public boolean isRunning = false;

    // Private constructor (singleton pattern)
    public CommunicatorClientWebSocket() {
        gson = new Gson();
    }

    /**
     * Get singleton instance of this class.
     * Ensure that only one instance of this class is created.
     * @return instance of client web socket
     */
    public static CommunicatorClientWebSocket getInstance() {
        if (instance == null) {
            System.out.println("[WebSocket Client create singleton instance]");
            instance = new CommunicatorClientWebSocket();
        }
        return instance;
    }

    @OnOpen
    public void onWebSocketConnect(Session session){
        System.out.println("[WebSocket Client open session] " + session.getRequestURI());
        this.session = session;
    }

    @OnMessage
    public void onWebSocketText(String message, Session session){
        this.message = message;
        System.out.println("[WebSocket Client message received] " + message);
        processMessage(message);
    }

    @OnError
    public void onWebSocketError(Session session, Throwable cause) {
        System.out.println("[WebSocket Client connection error] " + cause.toString());
    }

    @OnClose
    public void onWebSocketClose(CloseReason reason){
        System.out.print("[WebSocket Client close session] " + session.getRequestURI());
        System.out.println(" for reason " + reason);
        session = null;
    }

    public void sendMessageToServer(WebSocketMessage message) {
        String jsonMessage = gson.toJson(message);
        // Use asynchronous communication
        session.getAsyncRemote().sendText(jsonMessage);
    }

    /**
     * Get the latest message received from the websocket communication.
     * @return The message from the websocket communication
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message, but no action is taken when the message is changed.
     * @param message the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Start a WebSocket client.
     */
    public void startClient() {
        System.out.println("[WebSocket Client start]");
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(uri));

        } catch (IOException | URISyntaxException | DeploymentException ex) {
            // do something useful eventually
            ex.printStackTrace();
        }
    }

    /**
     * Stop the client when it is running.
     */
    public void stopClient(){
        System.out.println("[WebSocket Client stop]");
        try {
            session.close();

        } catch (IOException ex){
            // do something useful eventually
            ex.printStackTrace();
        }
    }

    // Process incoming json message
    public void processMessage(String jsonMessage) {

        // Parse incoming message
        WebSocketMessage wsMessage;
        WebSocketType operation;
        try {
            wsMessage = gson.fromJson(jsonMessage, WebSocketMessage.class);
            operation = wsMessage.getWebSocketType();
        }
        catch (JsonSyntaxException ex) {
            System.out.println("[WebSocket Client ERROR: cannot parse Json message " + jsonMessage);
            return;
        }

        if (null != operation) {
            switch (operation) {
                case REGISTERPLAYER:
                    //register player
                    SeaBattleGame.UI.setPlayerNumber(wsMessage.playerNr, wsMessage.name);
                    break;
                case REGISTEROPPONENT:
                    SeaBattleGame.UI.setOpponentName(wsMessage.playerNr, wsMessage.name);
                    break;
                case ERROR:
                    SeaBattleGame.UI.showErrorMessage(wsMessage.playerNr, wsMessage.errorMessage);
                    break;
                case FATALERROR:
                    System.out.println(wsMessage.getErrorMessage());
                    stopClient();
                    break;
                case SETSQUAREPLAYER:
                    SeaBattleGame.UI.showSquarePlayer(wsMessage.playerNr, wsMessage.x, wsMessage.y, wsMessage.squareState);
                    break;
                default:
                    System.out.println("[WebSocket ERROR: cannot process Json message " + jsonMessage);
                    break;
            }
        }


//        // Only operation update property will be further processed
//        WebSocketType operation;
//        operation = wsMessage.getWebSocketType();
//        if (operation == null || operation != CommunicatorWebSocketMessageOperation.UPDATEPROPERTY) {
//            System.out.println("[WebSocket Client ERROR: update property operation expected]");
//            return;
//        }
//
//        // Obtain property from message
//        String property = wsMessage.getProperty();
//        if (property == null || "".equals(property)) {
//            System.out.println("[WebSocket Client ERROR: property not defined]");
//            return;
//        }
//
//        // Obtain content from message
//        String content = wsMessage.getContent();
//        if (content == null || "".equals(content)) {
//            System.out.println("[WebSocket Client ERROR: message without content]");
//            return;
//        }
//
//        // Create instance of CommunicaterMessage for observers
//        WebSocketMessage commMessage = new WebSocketMessage();
//        commMessage.setProperty(property);
//        commMessage.setContent(content);
//
//        // Notify observers
//        this.setChanged();
//        this.notifyObservers(commMessage);
    }
}
