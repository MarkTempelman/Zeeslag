package seabattleserver;

import seabattleshared.*;
import servercommunicator.CommunicatorServerWebSocket;

import java.util.ArrayList;

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
        tryPlaceShip(playerNr, shipType, bowX, bowY, horizontal);
    }

    private void tryPlaceShip(int playerNr, ShipType shipType, int bowX, int bowY, boolean horizontal){
        Ship ship = new Ship(shipType, bowX, bowY, horizontal);
        Position pos1 = new Position(bowX, bowY);
        ship.addPositions(pos1);
        if(horizontal) {
            for(int i = 0; i < shipType.length; i++) {
                Position pos = new Position(bowX + i, bowY);
                ship.addPositions(pos);
            }
            placeShipIfPossible(playerNr, ship);
        } else {
            for(int i = 0; i < shipType.length; i++) {
                Position pos = new Position(bowX, bowY + i);
                ship.addPositions(pos);
            }
            placeShipIfPossible(playerNr, ship);
        }
    }

    private void placeShipIfPossible(int playerNr, Ship ship){
        if(canShipBePlaced(ship, playerNr)){
            for (Position position : ship.getPositions()) {
                communicator.sendMessageToPlayer(playerNr, new WebSocketMessage(
                        WebSocketType.PLACESHIP, playerNr, position.getX(), position.getY()
                    ));
            }
            shipManagers.get(playerNr).addShip(ship);
        } else {
            communicator.sendMessageToPlayer(playerNr, new WebSocketMessage(
                    WebSocketType.ERROR, "this ship can't be placed here"
            ));
        }
    }

    public boolean canShipBePlaced(Ship ship, int playerNr){
        for (Position position : ship.getPositions()) {
            if(checkIfOutOfBounds(position.getX(), position.getY()) || checkIfOnSquare(position.getX(), position.getY(), playerNr)){
                return false;
            }
        }
        return true;
    }

    public boolean checkIfOutOfBounds(int x, int y){
        return x > 9 || x < 0 || y > 9 || y < 0;
    }

    public boolean checkIfOnSquare(int x, int y, int playerNr){
        return shipManagers.get(playerNr).checkIfOverlap(x, y);
    }
}
