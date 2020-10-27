package seabattleai;

import seabattlegui.ISeaBattleGUI;
import seabattleshared.ShotType;
import seabattleshared.SquareState;

public class AISeaBattleApplication implements ISeaBattleGUI {

    int playerNr;

    @Override
    public int getPlayerNumber(){
        return playerNr;
    }

    @Override
    public void setPlayerNumber(int playerNr, String name) {
        this.playerNr = playerNr;
    }

    @Override
    public void setOpponentName(int playerNr, String name) {
        //visual only not necessary to implement
    }

    @Override
    public void notifyStartGame(int playerNr) {
        //visual only not necessary to implement
    }

    @Override
    public void playerFiresShot(int playerNr, ShotType shotType) {
        //visual only not necessary to implement
    }

    @Override
    public void opponentFiresShot(int playerNr, ShotType shotType) {
        //visual only not necessary to implement
    }

    @Override
    public void showSquarePlayer(int playerNr, int posX, int posY, SquareState squareState) {
        //visual only not necessary to implement
    }

    @Override
    public void showSquareOpponent(int playerNr, int posX, int posY, SquareState squareState) {
        //visual only not necessary to implement
    }

    @Override
    public void showErrorMessage(int playerNr, String errorMessage) {
        //visual only not necessary to implement
    }
}
