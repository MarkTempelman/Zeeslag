package seabattlegame;

import Models.Position;
import Models.Ship;
import seabattleshared.ShipType;
import seabattleshared.ShotType;

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

    private Ship tryGetShipAtPos(Position pos){
        for(Ship ship : allShips){
            for(Position position : ship.getPositions()){
                if(position.getX() == pos.getX() && position.getY() == pos.getY()){
                    return ship;
                }
            }
        }
        return null;
    }

    public ShotType receiveShot(int x, int y){
        Position pos = new Position(x, y);
        Ship ship = tryGetShipAtPos(pos);
        if(ship == null){
            return ShotType.MISSED;
        }
        ship.addHitPosition(pos);
        if(ship.getHitPositions().size() != ship.getPositions().size()){
            return ShotType.HIT;
        }
        if(areAllShipsSunk()){
            return ShotType.ALLSUNK;
        }
        return ShotType.SUNK;
    }

    private boolean areAllShipsSunk(){
        for (Ship ship : allShips){
            if(ship.getPositions().size() != ship.getHitPositions().size()){
                return false;
            }
        }
        return true;
    }

    public boolean shipTypeInList(ShipType type){
        for (Ship ship: allShips) {
            if(ship.getType() == type){
                return true;
            }
        }
        return false;
    }

    public List<Position> removeShip(Position pos){
        List<Position> positions = new ArrayList<>();
        for (Ship ship: allShips) {
            for (Position position : ship.getPositions()){
                if(pos == position){
                    positions = ship.getPositions();
                    allShips.remove(ship);
                    return positions;
                }
            }
        }
        return positions;
    }

    public List<Position> getAllPositions(){
        List<Position> positions = new ArrayList<>();
        for (Ship ship : allShips) {
            for (Position pos : ship.getPositions()) {
                positions.add(pos);
            }
        }
        return positions;
    }

    public List<Position> removeAllShips() {
        List<Position> allPositions = getAllPositions();
        allShips.clear();
        return allPositions;
    }

}
