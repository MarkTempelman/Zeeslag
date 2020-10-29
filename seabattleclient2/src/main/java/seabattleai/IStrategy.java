package seabattleai;

import seabattleshared.Ship;

import java.util.List;

public interface IStrategy {
    public List<Ship> placeShips();
}
