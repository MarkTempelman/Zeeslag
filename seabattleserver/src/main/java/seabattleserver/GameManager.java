package seabattleserver;

import seabattleshared.*;
import servercommunicator.ICommunicatorServerWebSocket;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static seabattleshared.GameHelper.shotTypeToSquareState;

public class GameManager {
    private ICommunicatorServerWebSocket communicator;

    private ArrayList<Player> players = new ArrayList<>();

    private boolean acceptsNewPlayers = true;

    public void registerPlayer(String name, Session session, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        int playerNr = players.size();
        Player player = new Player(session, playerNr, name, new ShipManager());
        players.add(player);
        communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.REGISTERPLAYER, name, playerNr));
        if(players.size() == 2){
            acceptsNewPlayers = false;
            Player opponent = getOpponent(playerNr);
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.REGISTEROPPONENT, opponent.getName(), playerNr));
            communicator.sendMessageToPlayer(opponent.getSession(), new WebSocketMessage(WebSocketType.REGISTEROPPONENT, player.getName(), opponent.getPlayerNr()));
        }
    }

    private Player getOpponent(int playerNr){
        return players.stream().filter(player -> player.getPlayerNr() != playerNr).findFirst().orElse(null);
    }

    private Player getPlayer(int playerNr){
        return players.stream().filter(player -> player.getPlayerNr() == playerNr).findFirst().orElse(null);
    }

    public void tryPlaceShip(int playerNr, boolean horizontal, ShipType shipType, int bowX, int bowY, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        ShipManager manager = getPlayer(playerNr).getShipManager();
        if(manager.shipTypeInList(shipType) || manager.allShips.size() >= 5){
            return;
        }
        Ship ship = GameHelper.createShip(shipType, bowX, bowY, horizontal);
        placeShipIfPossible(playerNr, ship);
    }

    private void placeShipIfPossible(int playerNr, Ship ship){
        Session session = getPlayer(playerNr).getSession();
        ShipManager manager = getPlayer(playerNr).getShipManager();
        if(GameHelper.canShipBePlaced(ship, manager)){
            for (Position position : ship.getPositions()) {
                communicator.sendMessageToPlayer(session, new WebSocketMessage(
                        WebSocketType.SETSQUAREPLAYER, playerNr, position.getX(), position.getY(), SquareState.SHIP
                    ));
            }
            manager.addShip(ship);
        } else {
            communicator.sendMessageToPlayer(session, new WebSocketMessage(
                    WebSocketType.ERROR, "this ship can't be placed here"
            ));
        }
    }

    public void removeShip(int playerNr, int posX, int posY, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        ShipManager manager = getPlayer(playerNr).getShipManager();
        List<Position> positions = manager.getAllPositions();
        List<Position> deletePositions;
        for (Position pos: positions) {
            if(pos.getX() == posX && pos.getY() == posY){
                deletePositions = manager.removeShip(pos);
                for (Position delete: deletePositions) {
                    communicator.sendMessageToPlayer(getPlayer(playerNr).getSession(), new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, delete.getX(), delete.getY(), SquareState.WATER));
                }
            }
        }
    }

    public void setSquareStateOnOverlap(int playerNr, int posX, int posY, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Session session = getPlayer(playerNr).getSession();
        ShipManager manager = getPlayer(playerNr).getShipManager();
        if(manager.checkIfOverlap(posX, posY)){
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, posX, posY, SquareState.SHIP));
        } else {
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, posX, posY, SquareState.WATER));
        }
    }

    public void removeAllShips(int playerNr, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Session session = getPlayer(playerNr).getSession();
        List<Position> positions = getPlayer(playerNr).getShipManager().removeAllShips();
        for(Position pos : positions){
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, pos.getX(), pos.getY(), SquareState.WATER));
        }
    }

    public void notifyWhenReady(int playerNr, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Session session = getPlayer(playerNr).getSession();
        ShipManager manager = getPlayer(playerNr).getShipManager();
        if(manager.allShips.size() == 5){
            readyPlayer(playerNr);
            if(players.stream().filter(Player::isReady).collect(Collectors.toList()).size() == 2){
                communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.STARTGAME, playerNr));
                Player opponent = getOpponent(playerNr);
                communicator.sendMessageToPlayer(opponent.getSession(), new WebSocketMessage(WebSocketType.STARTGAME, opponent.getPlayerNr()));
            }
        } else {
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.ERROR, playerNr, "Not all ships have been placed!"));
        }
    }

    private void readyPlayer(int playerNr){
        players.stream().filter(player -> player.getPlayerNr() == playerNr).findFirst().orElse(null).setReady(true);
    }

    public void fireShot(int playerNr, int posX, int posY, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Player player = getPlayer(playerNr);
        Player opponent = getOpponent(playerNr);
        ShotType shotType = opponent.getShipManager().receiveShot(posX, posY);
        SquareState squareState = shotTypeToSquareState(shotType);

        communicator.sendMessageToPlayer(player.getSession(), new WebSocketMessage(WebSocketType.PLAYERSHOT, playerNr, shotType));
        communicator.sendMessageToPlayer(opponent.getSession(), new WebSocketMessage(WebSocketType.OPPONENTSHOT, opponent.getPlayerNr(), shotType));

        communicator.sendMessageToPlayer(player.getSession(), new WebSocketMessage(WebSocketType.SETSQUAREOPPONENT, playerNr, posX, posY, squareState));
        communicator.sendMessageToPlayer(opponent.getSession(), new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, opponent.getPlayerNr(), posX, posY, squareState));
    }

    public void startNewGame(int playerNr, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;

        communicator.sendMessageToPlayer(getPlayer(playerNr).getSession(), new WebSocketMessage(WebSocketType.CLEARMAP, playerNr));

        communicator.closeSession(getPlayer(playerNr).getSession());
    }

    public boolean isAcceptsNewPlayers() {
        return acceptsNewPlayers;
    }

    public boolean containsPlayer(Session session){
        Player player = players.stream().filter(p -> p.getSession() == session).findFirst().orElse(null);
        if(player != null){
            return true;
        }
        return false;
    }
}
