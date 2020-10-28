package seabattleai;

import Models.Ship;

import java.util.List;

public interface IStrategy {
    public List<Ship> placeShips();
}
