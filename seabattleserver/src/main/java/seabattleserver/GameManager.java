package seabattleserver;

import seabattleshared.*;
import servercommunicator.CommunicatorServerWebSocket;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static ArrayList<String> playerNames = new ArrayList<>();
    private static ArrayList<ShipManager> shipManagers = new ArrayList<>();
    private CommunicatorServerWebSocket communicator;

    public ArrayList<String> getPlayerNames(){
        return playerNames;
    }

    public int registerPlayer(String name){
        int playerNumber = playerNames.size();
        playerNames.add(name);
        shipManagers.add(new ShipManager());
        return playerNumber;
    }

    public int getOpponentNumber(int playerNr){
        if(playerNr == 0){
            return 1;
        }
        return 0;
    }

    public void tryPlaceShip(int playerNr, boolean horizontal, ShipType shipType, int bowX, int bowY, CommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        ShipManager manager = shipManagers.get(playerNr);
        if(manager.shipTypeInList(shipType) || manager.allShips.size() >= 5){
            return;
        }
        Ship ship = GameHelper.createShip(shipType, bowX, bowY, horizontal);
        placeShipIfPossible(playerNr, ship);
    }

    private void placeShipIfPossible(int playerNr, Ship ship){
        if(GameHelper.canShipBePlaced(ship, shipManagers.get(playerNr))){
            for (Position position : ship.getPositions()) {
                communicator.sendMessageToPlayer(playerNr, new WebSocketMessage(
                        WebSocketType.SETSQUAREPLAYER, playerNr, position.getX(), position.getY(), SquareState.SHIP
                    ));
            }
            shipManagers.get(playerNr).addShip(ship);
        } else {
            communicator.sendMessageToPlayer(playerNr, new WebSocketMessage(
                    WebSocketType.ERROR, "this ship can't be placed here"
            ));
        }
    }

    public void removeShip(int playerNr, int posX, int posY, CommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        List<Position> positions = shipManagers.get(playerNr).getAllPositions();
        List<Position> deletePositions;
        for (Position pos: positions) {
            if(pos.getX() == posX && pos.getY() == posY){
                deletePositions = shipManagers.get(playerNr).removeShip(pos);
                for (Position delete: deletePositions) {
                    communicator.sendMessageToPlayer(playerNr, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, delete.getX(), delete.getY(), SquareState.WATER));
                }
            }
        }
    }

    public void setSquareStateOnOverlap(int playerNr, int posX, int posY, CommunicatorServerWebSocket communicator){
        this.communicator = communicator;
        if(shipManagers.get(playerNr).checkIfOverlap(posX, posY)){
            communicator.sendMessageToPlayer(playerNr, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, posX, posY, SquareState.SHIP));
        } else {
            communicator.sendMessageToPlayer(playerNr, new WebSocketMessage(WebSocketType.SETSQUAREPLAYER, playerNr, posX, posY, SquareState.WATER));
        }
    }
}
