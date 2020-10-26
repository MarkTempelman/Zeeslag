package seabattleai;

import Models.Position;
import seabattlegame.ISeaBattleGame;
import seabattlegame.SeaBattleGame;
import seabattlegui.ISeaBattleGUI;

import java.util.ArrayList;
import java.util.Random;

public class SeaBattleAI {
    ISeaBattleGUI application = new AISeaBattleApplication();
    int playerNr;
    ISeaBattleGame game;
    ArrayList<Position> previousShots = new ArrayList<>();

    public SeaBattleAI(ISeaBattleGame game){
        this.game = game;
    }

    public void SetupAI(){
        game.registerPlayer("AI", "password", application, true);
        playerNr = application.getPlayerNumber();
        game.placeShipsAutomatically(playerNr);
    }

    public void aiTurn(){
        Position pos = generateRandomPos();
        boolean duplicate = false;
        for(Position position : previousShots){
            if(position.getX() == pos.getX() && position.getY() == pos.getY()){
                duplicate = true;
                break;
            }
        }
        if(duplicate){
            aiTurn();
        } else {
            previousShots.add(pos);
            game.fireShot(playerNr, pos.getX(), pos.getY());
        }
    }

    private Position generateRandomPos(){
        Random r = new Random();
        return new Position(r.nextInt(10), r.nextInt(10));
    }
}
