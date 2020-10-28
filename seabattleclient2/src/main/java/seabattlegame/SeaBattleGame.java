/*
 * Sea Battle Start project.
 */
package seabattlegame;

import APILoginREST.APILogin;
import Models.Player;
import Models.Position;
import Models.Ship;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import seabattleai.IStrategy;
import seabattleai.SeaBattleAI;
import seabattleai.SimpleStrategy;
import seabattlegui.*;
import seabattleshared.ShipType;
import seabattleshared.ShotType;
import seabattleshared.SquareState;

import java.util.ArrayList;
import java.util.List;

/**
 * The Sea Battle game. To be implemented.
 *
 * @author Nico Kuijpers
 */
public class SeaBattleGame implements ISeaBattleGame {
  //private static final Logger log = LoggerFactory.getLogger(SeaBattleGame.class);
  ArrayList<ISeaBattleGUI> applications = new ArrayList<ISeaBattleGUI>();
  boolean singlePlayerMode;
  ArrayList<Player> players = new ArrayList<Player>();
  ArrayList<ShipManager> managers = new ArrayList<ShipManager>();
  SeaBattleAI AI = new SeaBattleAI(this);
  APILogin login = APILogin.getInstance();

  @Override
  public void registerPlayer(String name, String password, ISeaBattleGUI application, boolean singlePlayerMode ){
    if(name == null || password == null || name == "" || password =="")
    {
      throw new IllegalArgumentException("Username or Password is null");
    }
//    if(userLogin(name, password).contains("NOT_FOUND")){
//      throw new IllegalArgumentException("Username already exists.");
//    }
    if(singlePlayerMode){
      registerPlayerSingleplayer(name, password, application, singlePlayerMode);
    }
    else{
      registerPlayerMultiplayer(name);
    }
  }

  private void registerPlayerSingleplayer(String name, String password, ISeaBattleGUI application, boolean singlePlayerMode){
    int applicationSize = applications.size();
    if(applicationSize < 2){
      this.applications.add(application);
      this.singlePlayerMode = singlePlayerMode;
      players.add(new Player(name, password, applicationSize));
      applications.get(applicationSize).setPlayerNumber(players.get(applicationSize).getPlayerNumber(), players.get(applicationSize).getName());
      managers.add(new ShipManager());
    }
  }
  private void registerPlayerMultiplayer(String name){

  }

  private String userLogin(String name, String password) {
    String returnString;
    try {
      returnString = login.register(name, password);
      return returnString;
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public void placeShipsAutomatically(int playerNr) {
    if(singlePlayerMode){
      placeShipsAutomaticallySingleplayer(playerNr);
      return;
    }
    placeShipsAutomaticallyMultiplayer();

  }

  private void placeShipsAutomaticallySingleplayer(int playerNr) {
    IStrategy strategy = new SimpleStrategy();
    List<Ship> ships = strategy.placeShips();
    for (Ship ship: ships){
      placeShip(playerNr, ship.getType(), ship.getxBow(), ship.getyBow(), ship.isHorizontal());
    }
  }

  private void placeShipsAutomaticallyMultiplayer(){

  }


  @Override
  public void placeShip(int playerNr, ShipType shipType, int bowX, int bowY, boolean horizontal) {
    if(singlePlayerMode){
      placeShipSingleplayer(playerNr, shipType, bowX, bowY, horizontal);
      return;
    }
    placeShipMultiplayer();
  }

  private void placeShipSingleplayer(int playerNr, ShipType shipType, int bowX, int bowY, boolean horizontal) {
    ShipManager manager = managers.get(playerNr);
    if(manager.shipTypeInList(shipType) || manager.allShips.size() >= 5){
      return;
    }
    tryPlaceShip(playerNr, shipType, bowX, bowY, horizontal);
  }

  private void placeShipMultiplayer(){

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
   if(singlePlayerMode){
     removeShipSingleplayer(playerNr, posX, posY);
     return;
   }
    removeShipMultiplayer();

  }

  private void removeShipSingleplayer(int playerNr, int posX, int posY) {
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

  private void removeShipMultiplayer(){

  }



  @Override
  public void removeAllShips(int playerNr) {
   if(singlePlayerMode){
     removeAllShipsSingleplayer(playerNr);
     return;
   }
    removeAllShipsMultiplayer();
  }
  private void removeAllShipsSingleplayer(int playerNr) {
    List<Position> positions = managers.get(playerNr).removeAllShips();
    for (Position pos : positions) {
      applications.get(playerNr).showSquarePlayer(playerNr, pos.getX(), pos.getY(), SquareState.WATER);
    }
  }
  private void removeAllShipsMultiplayer(){

  }

  @Override
  public void notifyWhenReady(int playerNr) {
    if(singlePlayerMode){
      notifyWhenReadySingleplayer(playerNr);
      return;
    }
    notifyWhenReadyMultiplayer();
  }
  private void notifyWhenReadySingleplayer(int playerNr) {
    if(managers.get(playerNr).allShips.size() == 5){
        applications.get(playerNr).notifyStartGame(playerNr);
        AI.SetupAI();
    } else {
      applications.get(playerNr).showErrorMessage(playerNr, "Not all ships have been placed!");
    }
  }
  private void notifyWhenReadyMultiplayer(){

  }

  @Override
  public void fireShot(int playerNr, int posX, int posY) {
    if(singlePlayerMode){
      fireShotSingleplayer(playerNr, posX, posY);
      return;
    }
    fireShotMultiplayer();
  }
  private void fireShotSingleplayer(int playerNr, int posX, int posY) {
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
  private void fireShotMultiplayer(){

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
    if(singlePlayerMode){
      startNewGameSingleplayer(playerNr);
      return;
    }
    startNewGameMultiplayer();
  }
  private void startNewGameSingleplayer(int playerNr) {
    clearMap(playerNr);
    if(singlePlayerMode){
      applications = new ArrayList<>();
      players = new ArrayList<>();
      managers = new ArrayList<>();
      return;
    }
    applications.remove(playerNr);
    players.remove(playerNr);
    managers.remove(playerNr);
  }
  private void startNewGameMultiplayer(){

  }

  private void clearMap(int playerNr){
    for (int x = 0; x < 10; x++) {
      for (int y = 0; y < 10; y++) {
        applications.get(playerNr).showSquarePlayer(playerNr, x, y, SquareState.WATER);
        applications.get(playerNr).showSquareOpponent(playerNr, x, y, SquareState.WATER);
      }
    }
  }
}
