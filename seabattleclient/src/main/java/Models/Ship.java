package Models;

import seabattlegui.ShipType;

import java.util.ArrayList;
import java.util.List;

public class Ship {
    ShipType type;
    int yBow;
    int xBow;
    private boolean horizontal;

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public Ship(ShipType type, int x, int y, boolean horizontal){
        this.type = type;
        yBow = y;
        xBow = x;
        this.horizontal = horizontal;
    }

    public ShipType getType() {
        return type;
    }

    public void setType(ShipType type) {
        this.type = type;
    }

    public int getyBow() {
        return yBow;
    }

    public void setyBow(int yBow) {
        this.yBow = yBow;
    }

    public int getxBow() {
        return xBow;
    }

    public void setxBow(int xBow) {
        this.xBow = xBow;
    }

    private List<Position> positions = new ArrayList<>();

    public List<Position> getPositions() {
        return positions;
    }

    public void addPositions(Position pos) {
        this.positions.add(pos);
    }
}
