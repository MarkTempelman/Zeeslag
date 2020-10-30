package seabattleshared;

public class WebSocketMessage {
    public WebSocketType webSocketType;
    public String name;
    public String errorMessage;
    public int playerNr;
    public boolean horizontal;
    public ShipType shipType;
    public int x;
    public int y;
    public SquareState squareState;

    public WebSocketMessage(){

    }

    public WebSocketMessage(WebSocketType webSocketType, int playerNr) {
        this.webSocketType = webSocketType;
        this.playerNr = playerNr;
    }

    public WebSocketMessage(WebSocketType webSocketType, String errorMessage) {
        this.webSocketType = webSocketType;
        this.errorMessage = errorMessage;
    }

    public WebSocketMessage(WebSocketType webSocketType, int playerNr, String errorMessage){
        this.webSocketType = webSocketType;
        this.playerNr = playerNr;
        this.errorMessage = errorMessage;
    }

    public WebSocketMessage(WebSocketType webSocketType, String name, int playerNr) {
        this.webSocketType = webSocketType;
        this.name = name;
        this.playerNr = playerNr;
    }

    public WebSocketMessage(WebSocketType webSocketType, int playerNr, boolean horizontal, ShipType shipType, int x, int y) {
        this.webSocketType = webSocketType;
        this.playerNr = playerNr;
        this.horizontal = horizontal;
        this.shipType = shipType;
        this.x = x;
        this.y = y;
    }

    public WebSocketMessage(WebSocketType webSocketType, int playerNr, int x, int y) {
        this.webSocketType = webSocketType;
        this.playerNr = playerNr;
        this.x = x;
        this.y = y;
    }

    public WebSocketMessage(WebSocketType webSocketType, int playerNr, int x, int y, SquareState squareState) {
        this.webSocketType = webSocketType;
        this.playerNr = playerNr;
        this.x = x;
        this.y = y;
        this.squareState = squareState;
    }

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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getPlayerNr() {
        return playerNr;
    }

    public void setPlayerNr(int playerNr) {
        this.playerNr = playerNr;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public SquareState getSquareState() {
        return squareState;
    }

    public void setSquareState(SquareState squareState) {
        this.squareState = squareState;
    }
}
