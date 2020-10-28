package seabattleai;

import Models.Ship;
import seabattleshared.ShipType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimpleStrategy implements IStrategy{

    @Override
    public List<Ship> placeShips() {
        Ship AIRCRAFTCARRIER = new Ship(ShipType.AIRCRAFTCARRIER, 0, 0, true);
        Ship BATTLESHIP = new Ship(ShipType.BATTLESHIP, 0, 1, true);
        Ship CRUISER = new Ship(ShipType.CRUISER, 0, 2, true);
        Ship SUBMARINE = new Ship(ShipType.SUBMARINE, 0, 3, true);
        Ship MINESWEEPER = new Ship(ShipType.MINESWEEPER, 0, 4, true);

        Ship[] ships = new Ship[] {AIRCRAFTCARRIER, BATTLESHIP, CRUISER, SUBMARINE, MINESWEEPER};
        List<Ship> shipList = new ArrayList<>();
        shipList.addAll(Arrays.asList(ships));
        return shipList;
    }
}
