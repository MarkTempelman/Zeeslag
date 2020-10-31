package seabattlegame;

import seabattlegui.ISeaBattleGUI;
import seabattleshared.SquareState;

public class Helper {
    public static void clearMap(int playerNr, ISeaBattleGUI application){
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                application.showSquarePlayer(playerNr, x, y, SquareState.WATER);
                application.showSquareOpponent(playerNr, x, y, SquareState.WATER);
            }
        }
    }
}
