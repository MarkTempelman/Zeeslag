package seabattleshared;

public class WebSocketMessage {
    private WebSocketType webSocketType;
    private String name;

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
