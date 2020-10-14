package seabattlegame;

import Models.Position;
import Models.Ship;
import seabattlegui.ShipType;

import java.util.ArrayList;
import java.util.List;

public class ShipManager {
    List<Ship> allShips = new ArrayList<>();

    public List<Ship> getShips() {
        return allShips;
    }

    public void addShip(Ship ship) {
        this.allShips.add(ship);
    }
    public boolean checkIfOverlap(int x, int y){
        for (Ship ship: allShips) {
            for (Position pos: ship.getPositions()) {
                if(pos.getX() == x && pos.getY() == y){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean shipTypeInList(ShipType type){
        for (Ship ship: allShips) {
            if(ship.getType() == type){
                return true;
            }
        }
        return false;
    }

    public List<Position> removeAllShips() {
        List<Position> positions = new ArrayList<>();
        for (Ship ship : allShips) {
            for (Position pos : ship.getPositions()) {
                positions.add(pos);
            }
        }
        allShips.clear();
        return positions;
    }

}
