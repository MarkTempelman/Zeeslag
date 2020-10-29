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
        Ship ship = GameHelper.tryPlaceShip(shipType, bowX, bowY, horizontal);
        placeShipIfPossible(playerNr, ship);
    }

    private void placeShipIfPossible(int playerNr, Ship ship){
        if(GameHelper.canShipBePlaced(ship, shipManagers.get(playerNr))){
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
}
