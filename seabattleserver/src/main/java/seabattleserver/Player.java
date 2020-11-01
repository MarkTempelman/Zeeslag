package seabattleserver;

import javax.websocket.Session;

public class Player {
    private Session session;
    private int playerNr;
    private String name;

    public Player(Session session, int playerNr, String name) {
        this.session = session;
        this.playerNr = playerNr;
        this.name = name;
    }

    public Session getSession() {
        return session;
    }

    public int getPlayerNr() {
        return playerNr;
    }

    public String getName() {
        return name;
    }
}
