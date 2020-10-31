/*
 * Sea Battle Start project.
 */
package seabattlegame;

import APILoginREST.APILogin;
import Models.Player;
import seabattleshared.Position;
import seabattleshared.Ship;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import communication.CommunicatorClientWebSocket;
import seabattleai.IStrategy;
import seabattleai.SeaBattleAI;
import seabattleai.SimpleStrategy;
import seabattlegui.*;
import seabattleshared.*;

import java.util.ArrayList;
import java.util.List;

import static seabattlegame.Helper.clearMap;
import static seabattleshared.GameHelper.shotTypeToSquareState;

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
  private CommunicatorClientWebSocket communicator;
  public static ISeaBattleGUI UI;

  @Override
  public void registerPlayer(String name, String password, ISeaBattleGUI application, boolean singlePlayerMode ){
    if(name == null || password == null || name == "" || password =="" || application == null)
    {
      throw new IllegalArgumentException("Username or Password or Application is null");
    }
//    if(userLogin(name, password).contains("NOT_FOUND")){
//      throw new IllegalArgumentException("Username already exists.");
//    }
    if(singlePlayerMode){
      registerPlayerSingleplayer(name, password, application, singlePlayerMode);
    }
    else{
      UI = application;
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
    WebSocketMessage message = new WebSocketMessage();
    message.setName(name);
    message.setWebSocketType(WebSocketType.REGISTER);
    communicator = CommunicatorClientWebSocket.getInstance();
    communicator.startClient();
    communicator.sendMessageToServer(message);
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
    IStrategy strategy = new SimpleStrategy();
    List<Ship> ships = strategy.placeShips();
    for (Ship ship: ships){
      placeShip(playerNr, ship.getType(), ship.getxBow(), ship.getyBow(), ship.isHorizontal());
    }
  }

  @Override
  public void placeShip(int playerNr, ShipType shipType, int bowX, int bowY, boolean horizontal) {
    if(singlePlayerMode){
      placeShipSingleplayer(playerNr, shipType, bowX, bowY, horizontal);
      return;
    }
    placeShipMultiplayer(playerNr, shipType, bowX, bowY, horizontal);
  }

  private void placeShipSingleplayer(int playerNr, ShipType shipType, int bowX, int bowY, boolean horizontal) {
    ShipManager manager = managers.get(playerNr);
    if(manager.shipTypeInList(shipType) || manager.allShips.size() >= 5){
      return;
    }
    Ship ship = GameHelper.createShip(shipType, bowX, bowY, horizontal);
    placeShipIfPossible(playerNr, ship);
  }

  private void placeShipMultiplayer(int playerNr, ShipType shipType, int bowX, int bowY, boolean horizontal){
    WebSocketMessage message = new WebSocketMessage(WebSocketType.PLACESHIP, playerNr, horizontal, shipType, bowX, bowY);
    communicator.sendMessageToServer(message);
  }

  private void placeShipIfPossible(int playerNr, Ship ship){
    if(GameHelper.canShipBePlaced(ship, managers.get(playerNr))){
      for (Position position : ship.getPositions()) {
        applications.get(playerNr).showSquarePlayer(playerNr, position.getX(), position.getY(), SquareState.SHIP);
      }
      managers.get(playerNr).addShip(ship);
    } else {
      applications.get(playerNr).showErrorMessage(playerNr, "this ship can't be placed here");
    }
  }

  public boolean checkIfOnSquare(int x, int y, int playerNr){
    return managers.get(playerNr).checkIfOverlap(x, y);
  }

  public void setSquareStateOnOverlap(int x, int y, int playerNr){
    if(singlePlayerMode){
      if(checkIfOnSquare(x, y, playerNr)){
        applications.get(playerNr).showSquarePlayer(playerNr, x, y, SquareState.SHIP);
        return;
      }
      applications.get(playerNr).showSquarePlayer(playerNr, x, y, SquareState.WATER);
      return;
    }
    WebSocketMessage message = new WebSocketMessage(WebSocketType.CHECKOVERLAP, playerNr, x, y);
    communicator.sendMessageToServer(message);
  }

  @Override
  public void removeShip(int playerNr, int posX, int posY) {
   if(singlePlayerMode){
     removeShipSingleplayer(playerNr, posX, posY);
     return;
   }
    removeShipMultiplayer(playerNr, posX, posY);

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

  private void removeShipMultiplayer(int playerNr, int posX, int posY){
    WebSocketMessage message = new WebSocketMessage(WebSocketType.REMOVESHIP, playerNr, posX, posY);
    communicator.sendMessageToServer(message);
  }



  @Override
  public void removeAllShips(int playerNr) {
   if(singlePlayerMode){
     removeAllShipsSingleplayer(playerNr);
     return;
   }
    removeAllShipsMultiplayer(playerNr);
  }
  private void removeAllShipsSingleplayer(int playerNr) {
    List<Position> positions = managers.get(playerNr).removeAllShips();
    for (Position pos : positions) {
      applications.get(playerNr).showSquarePlayer(playerNr, pos.getX(), pos.getY(), SquareState.WATER);
    }
  }

  private void removeAllShipsMultiplayer(int playerNr){
    WebSocketMessage message = new WebSocketMessage(WebSocketType.REMOVEALLSHIPS, playerNr);
    communicator.sendMessageToServer(message);
  }

  @Override
  public void notifyWhenReady(int playerNr) {
    if(singlePlayerMode){
      notifyWhenReadySingleplayer(playerNr);
      return;
    }
    notifyWhenReadyMultiplayer(playerNr);
  }
  private void notifyWhenReadySingleplayer(int playerNr) {
    if(managers.get(playerNr).allShips.size() == 5){
        applications.get(playerNr).notifyStartGame(playerNr);
        AI.SetupAI();
    } else {
      applications.get(playerNr).showErrorMessage(playerNr, "Not all ships have been placed!");
    }
  }
  private void notifyWhenReadyMultiplayer(int playerNr){
    WebSocketMessage message = new WebSocketMessage(WebSocketType.READY, playerNr);
    communicator.sendMessageToServer(message);
  }

  @Override
  public void fireShot(int playerNr, int posX, int posY) {
    if(singlePlayerMode){
      fireShotSingleplayer(playerNr, posX, posY);
      return;
    }
    fireShotMultiplayer(playerNr, posX, posY);
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
  private void fireShotMultiplayer(int playerNr, int posX, int posY){
    WebSocketMessage message = new WebSocketMessage(WebSocketType.FIRESHOT, playerNr, posX, posY);
    communicator.sendMessageToServer(message);
  }

  private int getOpponentNumber(int playerNr){
    if(playerNr == 0){
      return 1;
    }
    return 0;
  }

  @Override
  public void startNewGame(int playerNr) {
    if(singlePlayerMode){
      startNewGameSingleplayer(playerNr);
      return;
    }
    startNewGameMultiplayer(playerNr);
  }
  private void startNewGameSingleplayer(int playerNr) {
    clearMap(playerNr, applications.get(playerNr));
    applications = new ArrayList<>();
    players = new ArrayList<>();
    managers = new ArrayList<>();
  }

  private void startNewGameMultiplayer(int playerNr){
    WebSocketMessage message = new WebSocketMessage(WebSocketType.STARTNEWGAME, playerNr);
    communicator.sendMessageToServer(message);
  }


}
