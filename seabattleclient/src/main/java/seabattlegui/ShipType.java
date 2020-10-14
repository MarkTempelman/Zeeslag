/*
 * Sea Battle Start project.
 */
package seabattlegui;

/**
 * Indicate type of ship.
 * @author Nico Kuijpers
 */
public enum ShipType {
    AIRCRAFTCARRIER(5),  // Aircraft carrier (size 5)
    BATTLESHIP(4),       // Battle ship (size 4)
    CRUISER(3),          // Cruister (size 3)
    SUBMARINE(3),        // Submarine (size 3)
    MINESWEEPER(2);      // Mine sweeper (size 2)

    public final int length;

    private ShipType(int length) {
        this.length = length;
    }
}
