package servercommunicator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import seabattleserver.GameManager;
import seabattleshared.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/communicator/")
public class CommunicatorServerWebSocket {
    // All sessions
    public static final List<Session> sessions = new ArrayList<>();

    // Map each property to list of sessions that are subscribed to that property
    public static final Map<String,List<Session>> propertySessions = new HashMap<>();

    private GameManager gameManager = new GameManager();

    private WebSocketMessage webSocketResponse;
    private Session currentSession;
    private Session otherSession;
    private Gson gson = new Gson();

    @OnOpen
    public void onConnect(Session session) {
        System.out.println("[WebSocket Connected] SessionID: " + session.getId());
        String message = String.format("[New client with client side session ID]: %s", session.getId());
        sessions.add(session);
        System.out.println("[#sessions]: " + sessions.size());
    }

    @OnMessage
    public void onText(String message, Session session) {
        System.out.println("[WebSocket Session ID] : " + session.getId() + " [Received] : " + message);
        handleMessageFromClient(message, session);
    }

    @OnClose
    public void onClose(CloseReason reason, Session session) {
        System.out.println("[WebSocket Session ID] : " + session.getId() + " [Socket Closed]: " + reason);
        sessions.remove(session);
    }

    @OnError
    public void onError(Throwable cause, Session session) {
        System.out.println("[WebSocket Session ID] : " + session.getId() + "[ERROR]: ");
        cause.printStackTrace(System.err);
    }

    // Handle incoming message from client
    public void handleMessageFromClient(String jsonMessage, Session session) {
        WebSocketMessage wbMessage = null;

        int numberOfSessions = sessions.size();

        try {
            wbMessage = gson.fromJson(jsonMessage,WebSocketMessage.class);
        }
        catch (JsonSyntaxException ex) {
            System.out.println("[WebSocket ERROR: cannot parse Json message " + jsonMessage);
            return;
        }

        WebSocketType operation;
        operation = wbMessage.getWebSocketType();

        if (null != operation) {
            switch (operation) {
                case REGISTER:
                    tryRegisterUser(numberOfSessions, wbMessage);
                    break;
                default:
                    System.out.println("[WebSocket ERROR: cannot process Json message " + jsonMessage);
                    break;
            }
        }
    }

    private void tryRegisterUser(int numberOfSessions, WebSocketMessage wbMessage){
        if(sessions.size() > 2){
            failedToRegisterUser(numberOfSessions);
            return;
        }
        int playerNr = gameManager.registerPlayer(wbMessage.name);
        registerUser(playerNr, false);
        if(playerNr == 1){
            registerUser(playerNr, true);
            registerUser(gameManager.getOpponentNumber(playerNr), true);
        }
    }

    private void failedToRegisterUser(int numberOfSessions){
        webSocketResponse = new WebSocketMessage();
        webSocketResponse.setWebSocketType(WebSocketType.ERROR);
        webSocketResponse.setErrorMessage("This lobby is full");
        currentSession = sessions.get(numberOfSessions - 1);
        currentSession.getAsyncRemote().sendText(gson.toJson(webSocketResponse));
    }

    private void registerUser(int playerNr, boolean isOpponent){
        webSocketResponse = new WebSocketMessage();
        currentSession = sessions.get(playerNr);
        webSocketResponse.setName(isOpponent ? gameManager.getPlayerNames().get(gameManager.getOpponentNumber(playerNr)) : gameManager.getPlayerNames().get(playerNr));
        webSocketResponse.setPlayerNr(playerNr);
        webSocketResponse.setWebSocketType(isOpponent ? WebSocketType.REGISTEROPPONENT : WebSocketType.REGISTERPLAYER);
        currentSession.getAsyncRemote().sendText(gson.toJson(webSocketResponse));
    }
}
