package seabattleserver;

import seabattleshared.ShipManager;

import javax.websocket.Session;

public class Player {
    private Session session;
    private int playerNr;
    private String name;
    private boolean ready;
    private ShipManager shipManager;

    public Player(Session session, int playerNr, String name, ShipManager shipManager) {
        this.session = session;
        this.playerNr = playerNr;
        this.name = name;
        this.shipManager = shipManager;
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public ShipManager getShipManager() {
        return shipManager;
    }
}
