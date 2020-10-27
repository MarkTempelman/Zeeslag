module seabattleserver {
    requires slf4j.api; // logging with Logback
    requires seabattleshared;
    requires org.eclipse.jetty.websocket.server;
    requires org.eclipse.jetty.server;
    requires javax.websocket.api;
    requires org.eclipse.jetty.servlet;
    requires org.eclipse.jetty.websocket.javax.websocket.server;
    requires gson;
}