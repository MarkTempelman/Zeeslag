package seabattleai;

import seabattlegame.ISeaBattleGame;
import seabattlegame.SeaBattleGame;
import seabattlegui.ISeaBattleGUI;

public class SeaBattleAI {
    ISeaBattleGUI application = new AISeaBattleApplication();
    int playerNr;
    ISeaBattleGame game;
    public SeaBattleAI(ISeaBattleGame game){
        this.game = game;
    }

    public void SetupAI(){
        game.registerPlayer("AI", "password", application, true);
        playerNr = application.getPlayerNumber();
        game.placeShipsAutomatically(playerNr);
    }
}
