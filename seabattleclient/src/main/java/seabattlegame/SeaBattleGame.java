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
import seabattleai.SeaBattleAI;
import seabattleai.SimpleStrategy;
import seabattlegui.ISeaBattleGUI;
import seabattlegui.ShipType;
import seabattlegui.ShotType;
import seabattlegui.SquareState;

import java.util.ArrayList;
import java.util.List;

/**
 * The Sea Battle game. To be implemented.
 *
 * @author Nico Kuijpers
 */
public class SeaBattleGame implements ISeaBattleGame {
  //TODO: refactor so SeaBattleGame can keep track of two players
  private static final Logger log = LoggerFactory.getLogger(SeaBattleGame.class);
  ArrayList<ISeaBattleGUI> applications = new ArrayList<ISeaBattleGUI>();
  boolean singlePlayerMode;
  ArrayList<Player> players = new ArrayList<Player>();
  ArrayList<ShipManager> managers = new ArrayList<ShipManager>();
  SeaBattleAI AI = new SeaBattleAI(this);

  @Override
  public void registerPlayer(String name, String password, ISeaBattleGUI application, boolean singlePlayerMode ) {
    //TODO: get player from Server. And check if name is unique, check if there are 2 players.
    //log.debug("Register Player {} - password {}", name, password);
<<<<<<< Updated upstream
    if(name == null || password == null || name == "" || password =="")
=======
    if(name == null || password == null || name == "" || password == "")
>>>>>>> Stashed changes
    {
      throw new IllegalArgumentException("Username is null");
    }
    int applicationSize = applications.size();
    if(applicationSize < 2){
      this.applications.add(application);
      this.singlePlayerMode = singlePlayerMode;
      players.add(new Player(name, password, applicationSize));
      applications.get(applicationSize).setPlayerNumber(players.get(applicationSize).getPlayerNumber(), players.get(applicationSize).getName());
      managers.add(new ShipManager());
    }
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
    ShipManager manager = managers.get(playerNr);
    if(manager.shipTypeInList(shipType) || manager.allShips.size() >= 5){
      return;
    }
    tryPlaceShip(playerNr, shipType, bowX, bowY, horizontal);
  }

  private void tryPlaceShip(int playerNr, ShipType shipType, int bowX, int bowY, boolean horizontal){
    Ship ship = new Ship(shipType, bowX, bowY, horizontal);
    Position pos1 = new Position(bowX, bowY);
    ship.addPositions(pos1);
    if(horizontal) {
      for(int i = 0; i < shipType.length; i++) {
        Position pos = new Position(bowX + i, bowY);
        ship.addPositions(pos);
      }
      placeShipIfPossible(playerNr, ship);
    } else {
      for(int i = 0; i < shipType.length; i++) {
        Position pos = new Position(bowX, bowY + i);
        ship.addPositions(pos);
      }
      placeShipIfPossible(playerNr, ship);
    }
  }

  private void placeShipIfPossible(int playerNr, Ship ship){
    if(canShipBePlaced(ship, playerNr)){
      for (Position position : ship.getPositions()) {
        applications.get(playerNr).showSquarePlayer(playerNr, position.getX(), position.getY(), SquareState.SHIP);
      }
      managers.get(playerNr).addShip(ship);
    } else {
      applications.get(playerNr).showErrorMessage(playerNr, "this ship can't be placed here");
    }
  }

  public boolean canShipBePlaced(Ship ship, int playerNr){
    for (Position position : ship.getPositions()) {
      if(checkIfOutOfBounds(position.getX(), position.getY()) || checkIfOnSquare(position.getX(), position.getY(), playerNr)){
        return false;
      }
    }
    return true;
  }

  public boolean checkIfOutOfBounds(int x, int y){
    return x > 9 || x < 0 || y > 9 || y < 0;
  }

  public boolean checkIfOnSquare(int x, int y, int playerNr){
    return managers.get(playerNr).checkIfOverlap(x, y);
  }

  @Override
  public void removeShip(int playerNr, int posX, int posY) {
    List<Position> positions = managers.get(playerNr).getAllPositions();
    List<Position> deletePositions = managers.get(playerNr).getAllPositions();
    for (Position pos: positions) {
      if(pos.getX() == posX && pos.getY() == posY){
        deletePositions = managers.get(playerNr).removeShip(pos);
        for (Position delete: deletePositions) {
          applications.get(playerNr).showSquarePlayer(playerNr, delete.getX(), delete.getY(), SquareState.WATER);
        }
      }
    }

  }

  @Override
  public void removeAllShips(int playerNr) {
    List<Position> positions = managers.get(playerNr).removeAllShips();
    for (Position pos : positions) {
      applications.get(playerNr).showSquarePlayer(playerNr, pos.getX(), pos.getY(), SquareState.WATER);
    }
  }

  @Override
  public void notifyWhenReady(int playerNr) {
    if(managers.get(playerNr).allShips.size() == 5){
      if(singlePlayerMode){
        applications.get(playerNr).notifyStartGame(playerNr);
        AI.SetupAI();
      }
    } else {
      applications.get(playerNr).showErrorMessage(playerNr, "Not all ships have been placed!");
    }
  }

  @Override
  public void fireShot(int playerNr, int posX, int posY) {
    ShotType shotType;
    int opponentNumber = getOpponentNumber(playerNr);
    shotType = managers.get(opponentNumber).receiveShot(posX, posY);

    applications.get(playerNr).playerFiresShot(playerNr, shotType);
    applications.get(opponentNumber).opponentFiresShot(opponentNumber, shotType);

    applications.get(playerNr).showSquareOpponent(playerNr,posX, posY, shotTypeToSquareState(shotType));
    applications.get(opponentNumber).showSquarePlayer(opponentNumber, posX, posY, shotTypeToSquareState(shotType));
    if(singlePlayerMode && playerNr == 0){
      AI.aiTurn();
    }
  }

  private int getOpponentNumber(int playerNr){
    if(playerNr == 0){
      return 1;
    }
    return 0;
  }

  private SquareState shotTypeToSquareState(ShotType shotType){
    switch(shotType){
      case MISSED:
        return SquareState.SHOTMISSED;
      case HIT:
        return SquareState.SHOTHIT;
      case SUNK:
      case ALLSUNK:
        return SquareState.SHIPSUNK;
    }
    return SquareState.SHOTMISSED;
  }

  @Override
  public void startNewGame(int playerNr) {
    throw new UnsupportedOperationException("Method startNewGame() not implemented.");
  }
}
