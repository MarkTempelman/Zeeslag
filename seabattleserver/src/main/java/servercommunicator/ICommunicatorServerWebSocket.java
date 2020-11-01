package servercommunicator;

import seabattleshared.WebSocketMessage;

public interface ICommunicatorServerWebSocket {
    void sendMessageToPlayer(int playerNr, WebSocketMessage webSocketMessage);
    void closeSession(int playerNr);
}
