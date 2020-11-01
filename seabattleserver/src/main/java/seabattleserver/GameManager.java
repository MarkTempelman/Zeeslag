package seabattleserver;

import seabattleshared.*;
import servercommunicator.CommunicatorServer;
import servercommunicator.CommunicatorServerWebSocket;
import servercommunicator.ICommunicatorServerWebSocket;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

import static seabattleshared.GameHelper.shotTypeToSquareState;

public class GameManager {
    private ArrayList<ShipManager> shipManagers = new ArrayList<>();
    private ICommunicatorServerWebSocket communicator;

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<java.lang.Integer> readyPlayers = new ArrayList<>();

    private boolean acceptsNewPlayers = true;

    public void registerPlayer(String name, int playerNr, Session session, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Player player = new Player(session, playerNr, name);
        players.add(player);
        shipManagers.add(new ShipManager());
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
        ShipManager manager = shipManagers.get(playerNr);
        if(manager.shipTypeInList(shipType) || manager.allShips.size() >= 5){
            return;
        }
        Ship ship = GameHelper.createShip(shipType, bowX, bowY, horizontal);
        placeShipIfPossible(playerNr, ship);
    }

    private void placeShipIfPossible(int playerNr, Ship ship){
        Session session = getPlayer(playerNr).getSession();
        if(GameHelper.canShipBePlaced(ship, shipManagers.get(playerNr))){
            for (Position position : ship.getPositions()) {
                communicator.sendMessageToPlayer(session, new WebSocketMessage(
                        WebSocketType.SETSQUAREPLAYER, playerNr, position.getX(), position.getY(), SquareState.SHIP
                    ));
            }
            shipManagers.get(playerNr).addShip(ship);
        } else {
            communicator.sendMessageToPlayer(session, new WebSocketMessage(
                    WebSocketType.ERROR, "this ship can't be placed here"
            ));
        }
    }

    public void removeShip(int playerNr, int posX, int posY, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        List<Position> positions = shipManagers.get(playerNr).getAllPositions();
        List<Position> deletePositions;
        for (Position pos: positions) {
            if(pos.getX() == posX && pos.getY() == posY){
                deletePositions = shipManagers.get(playerNr).removeShip(pos);
                for (Position delete: deletePositions) {
                    communicator.sendMessageToPlayer(getPlayer(playerNr).getSession(), new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, delete.getX(), delete.getY(), SquareState.WATER));
                }
            }
        }
    }

    public void setSquareStateOnOverlap(int playerNr, int posX, int posY, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Session session = getPlayer(playerNr).getSession();
        if(shipManagers.get(playerNr).checkIfOverlap(posX, posY)){
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, posX, posY, SquareState.SHIP));
        } else {
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, posX, posY, SquareState.WATER));
        }
    }

    public void removeAllShips(int playerNr, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Session session = getPlayer(playerNr).getSession();
        List<Position> positions = shipManagers.get(playerNr).removeAllShips();
        for(Position pos : positions){
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, pos.getX(), pos.getY(), SquareState.WATER));
        }
    }

    public void notifyWhenReady(int playerNr, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Session session = getPlayer(playerNr).getSession();
        if(shipManagers.get(playerNr).allShips.size() == 5){
            readyPlayer(playerNr);
            if(readyPlayers.size() == 2){
                communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.STARTGAME, playerNr));
                Player opponent = getOpponent(playerNr);
                communicator.sendMessageToPlayer(opponent.getSession(), new WebSocketMessage(WebSocketType.STARTGAME, opponent.getPlayerNr()));
            }
        } else {
            communicator.sendMessageToPlayer(session, new WebSocketMessage(WebSocketType.ERROR, playerNr, "Not all ships have been placed!"));
        }
    }

    private void readyPlayer(int playerNr){
        if(!readyPlayers.contains(playerNr)){
            readyPlayers.add(playerNr);
        }
    }

    public void fireShot(int playerNr, int posX, int posY, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        Player player = getPlayer(playerNr);
        Player opponent = getOpponent(playerNr);
        ShotType shotType = shipManagers.get(opponent.getPlayerNr()).receiveShot(posX, posY);
        SquareState squareState = shotTypeToSquareState(shotType);

        communicator.sendMessageToPlayer(player.getSession(), new WebSocketMessage(WebSocketType.PLAYERSHOT, playerNr, shotType));
        communicator.sendMessageToPlayer(opponent.getSession(), new WebSocketMessage(WebSocketType.OPPONENTSHOT, opponent.getPlayerNr(), shotType));

        communicator.sendMessageToPlayer(player.getSession(), new WebSocketMessage(WebSocketType.SETSQUAREOPPONENT, playerNr, posX, posY, squareState));
        communicator.sendMessageToPlayer(opponent.getSession(), new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, opponent.getPlayerNr(), posX, posY, squareState));
    }

    public void startNewGame(int playerNr, ICommunicatorServerWebSocket communicator){
        this.communicator = communicator;

        communicator.sendMessageToPlayer(getPlayer(playerNr).getSession(), new WebSocketMessage(WebSocketType.CLEARMAP, playerNr));
        shipManagers.remove(playerNr);
        readyPlayers.remove(playerNr);
        communicator.closeSession(playerNr);
    }

    public boolean isAcceptsNewPlayers() {
        return acceptsNewPlayers;
    }
}
