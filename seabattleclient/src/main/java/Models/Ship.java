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
        for(Position position : positions){
            if(position.getX() == pos.getX() && position.getY() == pos.getY()){
                return;
            }
        }
        this.positions.add(pos);
    }

    private List<Position> hitPositions = new ArrayList<>();

    public List<Position> getHitPositions(){
        return hitPositions;
    }

    public void addHitPosition(Position pos){
        for(Position position : hitPositions){
            if(position.getX() == pos.getX() && position.getY() == pos.getY()){
                return;
            }
        }
        hitPositions.add(pos);
    }
}
