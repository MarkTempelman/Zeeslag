/*
 * Sea Battle Start project.
 */
package seabattlegame;

import Logica.GameServer;
import Models.Player;
import Models.Position;
import Models.Ship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seabattleai.IStrategy;
import seabattleai.SimpleStrategy;
import seabattlegui.ISeaBattleGUI;
import seabattlegui.ShipType;
import seabattlegui.SquareState;

import java.util.List;

/**
 * The Sea Battle game. To be implemented.
 *
 * @author Nico Kuijpers
 */
public class SeaBattleGame implements ISeaBattleGame {

  private static final Logger log = LoggerFactory.getLogger(SeaBattleGame.class);
  ISeaBattleGUI application;
  boolean singlePlayerMode;
  Player player;
  ShipManager manager = new ShipManager();

  @Override
  public void registerPlayer(String name, String password, ISeaBattleGUI application, boolean singlePlayerMode ) {
    //TODO: get player from Server. And check if name is unique, check if there are 2 players.
    //log.debug("Register Player {} - password {}", name, password);
    if(name == null || password == null)
    {
      throw new IllegalArgumentException();
    }
    this.application = application;
    this.singlePlayerMode = singlePlayerMode;
    player = new Player(name, password, 0);
    this.application.setPlayerNumber(this.player.getPlayerNumber(), this.player.getName());
  }

  @Override
  public void placeShipsAutomatically(int playerNr) {
    IStrategy strategy = new SimpleStrategy();
    List<Ship> ships = strategy.placeShips();
    for (Ship ship: ships){
      placeShip(playerNr, ship.getType(), ship.getxBow(), ship.getyBow(), ship.isHorizontal());
    }
  }

  @Override
  public void placeShip(int playerNr, ShipType shipType, int bowX, int bowY, boolean horizontal) {
    //TODO: Check if out of bounds && if overlapping.
    if(manager.shipTypeInList(shipType) || manager.allShips.size() >= 5){
      return;
    }
    Ship ship;
    Position pos1;
    ship = new Ship(shipType, bowX, bowY, horizontal);
    pos1 = new Position(bowX, bowY);
    ship.addPositions(pos1);
    if(horizontal) {
      for(int i = 0; i < shipType.length; i++) {
        Position pos = new Position(bowX + i, bowY);
        ship.addPositions(pos);
      }
      if(canShipBePlaced(ship)){
        for (Position position : ship.getPositions()) {
          application.showSquarePlayer(playerNr, position.getX(), position.getY(), SquareState.SHIP);
        }
        manager.addShip(ship);
      } else {
        application.showErrorMessage(playerNr, "this ship can't be placed here");
      }
    } else {
      for(int i = 0; i < shipType.length; i++) {
        Position pos = new Position(bowX, bowY + i);
        ship.addPositions(pos);
      }
      if(canShipBePlaced(ship)){
        for (Position position : ship.getPositions()) {
          application.showSquarePlayer(playerNr, position.getX(), position.getY(), SquareState.SHIP);
        }
        manager.addShip(ship);
      } else {
        application.showErrorMessage(playerNr, "this ship can't be placed here");
      }
    }
  }

  public boolean canShipBePlaced(Ship ship){
    for (Position position : ship.getPositions()) {
      if(checkIfOutOfBounds(position.getX(), position.getY()) || checkIfOnSquare(position.getX(), position.getY())){
        return false;
      }
    }
    return true;
  }

  public boolean checkIfOutOfBounds(int x, int y){
    return x > 9 || x < 0 || y > 9 || y < 0;
  }

  public boolean checkIfOnSquare(int x, int y){
    return manager.checkIfOverlap(x, y);
  }

  @Override
  public void removeShip(int playerNr, int posX, int posY) {
    List<Position> positions = manager.getAllPositions();
    List<Position> deletePositions = manager.getAllPositions();
    for (Position pos: positions) {
      if(pos.getX() == posX && pos.getY() == posY){
        deletePositions = manager.removeShip(pos);
        for (Position delete: deletePositions) {
          application.showSquarePlayer(playerNr, delete.getX(), delete.getY(), SquareState.WATER);
        }
      }
    }
  }

  @Override
  public void removeAllShips(int playerNr) {
    List<Position> positions = manager.removeAllShips();
    for (Position pos : positions) {
      application.showSquarePlayer(playerNr, pos.getX(), pos.getY(), SquareState.WATER);
    }
  }

  @Override
  public void notifyWhenReady(int playerNr) {
    throw new UnsupportedOperationException("Method notifyWhenReady() not implemented.");
    //is ie echt ready???? zo niet: seabtalleapplication.showErrorMessage();
  }

  @Override
  public void fireShot(int playerNr, int posX, int posY) {
    throw new UnsupportedOperationException("Method fireShot() not implemented.");
  }

  @Override
  public void startNewGame(int playerNr) {
    throw new UnsupportedOperationException("Method startNewGame() not implemented.");
  }
}
