package seabattleshared;

public class GameHelper {
    public static Ship createShip(ShipType shipType, int bowX, int bowY, boolean horizontal){
        Ship ship = new Ship(shipType, bowX, bowY, horizontal);
        Position pos1 = new Position(bowX, bowY);
        ship.addPositions(pos1);
        if(horizontal) {
            for(int i = 0; i < shipType.length; i++) {
                Position pos = new Position(bowX + i, bowY);
                ship.addPositions(pos);
            }
        } else {
            for(int i = 0; i < shipType.length; i++) {
                Position pos = new Position(bowX, bowY + i);
                ship.addPositions(pos);
            }
        }
        return ship;
    }

    public static boolean canShipBePlaced(Ship ship, ShipManager manager){
        for (Position position : ship.getPositions()) {
            if(checkIfOutOfBounds(position.getX(), position.getY()) || checkIfOnSquare(position.getX(), position.getY(), manager)){
                return false;
            }
        }
        return true;
    }

    private static boolean checkIfOutOfBounds(int x, int y){
        return x > 9 || x < 0 || y > 9 || y < 0;
    }

    private static boolean checkIfOnSquare(int x, int y, ShipManager manager){
        return manager.checkIfOverlap(x, y);
    }
}
