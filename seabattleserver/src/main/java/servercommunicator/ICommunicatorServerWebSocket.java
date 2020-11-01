package servercommunicator;

import seabattleshared.WebSocketMessage;

import javax.websocket.Session;

public interface ICommunicatorServerWebSocket {
    void sendMessageToPlayer(Session session, WebSocketMessage webSocketMessage);
    void closeSession(int playerNr);
}
