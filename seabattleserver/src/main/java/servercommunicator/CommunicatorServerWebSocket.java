package servercommunicator;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import seabattleserver.GameManager;
import seabattleshared.*;

import java.io.IOException;
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
public class CommunicatorServerWebSocket implements ICommunicatorServerWebSocket{
    // All sessions
    public static final List<Session> sessions = new ArrayList<>();

    // Map each property to list of sessions that are subscribed to that property
    public static final Map<String,List<Session>> propertySessions = new HashMap<>();

    private static GameManager gameManager = new GameManager();
    private static ArrayList<GameManager> gameManagers = new ArrayList<>();

    private static int lastPlayerNr = -1;

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
                    tryRegisterUser(session, wbMessage.name);
                    break;
                case PLACESHIP:
                    gameManager.tryPlaceShip(wbMessage.playerNr, wbMessage.horizontal, wbMessage.shipType, wbMessage.x, wbMessage.y, this);
                    break;
                case REMOVESHIP:
                    gameManager.removeShip(wbMessage.playerNr, wbMessage.x, wbMessage.y, this);
                    break;
                case CHECKOVERLAP:
                    gameManager.setSquareStateOnOverlap(wbMessage.playerNr, wbMessage.x, wbMessage.y, this);
                    break;
                case REMOVEALLSHIPS:
                    gameManager.removeAllShips(wbMessage.playerNr, this);
                    break;
                case READY:
                    gameManager.notifyWhenReady(wbMessage.playerNr, this);
                    break;
                case FIRESHOT:
                    gameManager.fireShot(wbMessage.playerNr, wbMessage.x, wbMessage.y, this);
                    break;
                case STARTNEWGAME:
                    gameManager.startNewGame(wbMessage.playerNr, this);
                    break;
                default:
                    System.out.println("[WebSocket ERROR: cannot process Json message " + jsonMessage);
                    break;
            }
        }
    }

    private void tryRegisterUser(Session session, String name){
        GameManager gameManager = gameManagers.stream().filter(GameManager::isAcceptsNewPlayers).findFirst().orElse(null);
        int playerNr = lastPlayerNr + 1;
        lastPlayerNr++;
        if(gameManager == null){
            gameManager = new GameManager();
            gameManager.registerPlayer(name, playerNr, session, this);
            gameManagers.add(gameManager);
        } else {
            gameManager.registerPlayer(name, playerNr, session, this);
        }
    }

//    private void tryRegisterUser(int numberOfSessions, WebSocketMessage wbMessage){
//        if(sessions.size() > 2){
//            failedToRegisterUser(numberOfSessions);
//            return;
//        }
//        int playerNr = gameManager.registerPlayer(wbMessage.name);
//        registerUser(playerNr, false);
//        if(playerNr == 1){
//            registerUser(playerNr, true);
//            registerUser(gameManager.getOpponentNumber(playerNr), true);
//        }
//    }

//    private void failedToRegisterUser(int numberOfSessions){
//        webSocketResponse = new WebSocketMessage(WebSocketType.FATALERROR, "This lobby is full");
//        sendMessageToPlayer(numberOfSessions - 1, webSocketResponse);
//    }
//
//    private void registerUser(int playerNr, boolean isOpponent){
//        webSocketResponse = new WebSocketMessage(
//                isOpponent ? WebSocketType.REGISTEROPPONENT : WebSocketType.REGISTERPLAYER,
//                isOpponent ? gameManager.getPlayerNames().get(gameManager.getOpponentNumber(playerNr)) : gameManager.getPlayerNames().get(playerNr),
//                playerNr
//        );
//        sendMessageToPlayer(playerNr, webSocketResponse);
//    }

    public void sendMessageToPlayer(Session session, WebSocketMessage webSocketMessage){
        session.getAsyncRemote().sendText(gson.toJson(webSocketMessage));
    }

    public void closeSession(int playerNr){
        try{
            sessions.get(playerNr).close();
            sessions.remove(playerNr);
        } catch(IOException e){
            System.out.println(e);
        }

    }
}
