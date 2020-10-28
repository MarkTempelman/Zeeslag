module seabattleclient2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires gson;
    requires java.sql;
    opens seabattlegui to javafx.fxml;
    requires seabattleshared;
    requires javax.websocket.client.api;
    exports seabattlegui;
    opens APILoginREST;
}