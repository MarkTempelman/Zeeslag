package seabattleshared;

public class WebSocketMessage {
    public WebSocketType webSocketType;
    public String name;

    public WebSocketType getWebSocketType() {
        return webSocketType;
    }

    public void setWebSocketType(WebSocketType webSocketType) {
        this.webSocketType = webSocketType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
