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
    Ship ship;
    Position pos1;
    switch(shipType){
      case AIRCRAFTCARRIER:
        ship = new Ship(ShipType.AIRCRAFTCARRIER, bowX, bowY, horizontal);
        pos1 = new Position(bowX, bowY);
        ship.addPositions(pos1);
        for(int i = 0; i < 5; i++){
          if(horizontal){
            Position pos = new Position(bowX + i, bowY);
            ship.addPositions(pos);
            application.showSquarePlayer(playerNr, bowX + i, bowY, SquareState.SHIP);
          }
          else{
            Position pos = new Position(bowX, bowY + i);
            ship.addPositions(pos);
            application.showSquarePlayer(playerNr, bowX, bowY + i, SquareState.SHIP);
          }

        }
        manager.addShip(ship);
        break;
      case BATTLESHIP:
        ship = new Ship(ShipType.AIRCRAFTCARRIER, bowX, bowY, horizontal);
        pos1 = new Position(bowX, bowY);
        ship.addPositions(pos1);
        for(int i = 0; i < 4; i++){
          if(horizontal){
            Position pos = new Position(bowX + i, bowY);
            ship.addPositions(pos);
            application.showSquarePlayer(playerNr, bowX + i, bowY, SquareState.SHIP);
          }
          else{
            Position pos = new Position(bowX, bowY + i);
            ship.addPositions(pos);
            application.showSquarePlayer(playerNr, bowX, bowY + i, SquareState.SHIP);
          }

        }
        manager.addShip(ship);
        break;
      case CRUISER:
      case SUBMARINE:
        ship = new Ship(ShipType.AIRCRAFTCARRIER, bowX, bowY, horizontal);
        pos1 = new Position(bowX, bowY);
        for(int i = 0; i < 3; i++){
          if(horizontal){
            Position pos = new Position(bowX + i, bowY);
            ship.addPositions(pos);
            application.showSquarePlayer(playerNr, bowX + i, bowY, SquareState.SHIP);
          }
          else{
            Position pos = new Position(bowX, bowY + i);
            ship.addPositions(pos);
            application.showSquarePlayer(playerNr, bowX, bowY + i, SquareState.SHIP);
          }

        }
        manager.addShip(ship);
        break;
      case MINESWEEPER:
        ship = new Ship(ShipType.AIRCRAFTCARRIER, bowX, bowY, horizontal);
        pos1 = new Position(bowX, bowY);
        for(int i = 0; i < 2; i++){
          if(horizontal){
            Position pos = new Position(bowX + i, bowY);
            ship.addPositions(pos);
            application.showSquarePlayer(playerNr, bowX + i, bowY, SquareState.SHIP);
          }
          else{
            Position pos = new Position(bowX, bowY + i);
            ship.addPositions(pos);
            application.showSquarePlayer(playerNr, bowX, bowY + i, SquareState.SHIP);
          }
        }
        manager.addShip(ship);
        break;
    }
  }


  public boolean checkIfOnSquare(int x, int y){
    return manager.checkIfOverlap(x, y);
  }

  @Override
  public void removeShip(int playerNr, int posX, int posY) {
    throw new UnsupportedOperationException("Method removeShip() not implemented.");
  }

  @Override
  public void removeAllShips(int playerNr) {
    throw new UnsupportedOperationException("Method removeAllShips() not implemented.");

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
