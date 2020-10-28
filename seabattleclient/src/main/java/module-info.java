module seabattleclient {
  opens APILoginREST;
  requires slf4j.api;
  requires javafx.graphics;
  requires javafx.controls;
  requires seabattleshared;
  requires javax.websocket.client.api;
    requires gson;
    requires java.sql;


    exports seabattlegui;
}