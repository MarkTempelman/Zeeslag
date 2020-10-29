package seabattleserver;

import java.util.ArrayList;

public class GameManager {
    private static ArrayList<String> playerNames = new ArrayList<>();

    public ArrayList<String> getPlayerNames(){
        return playerNames;
    }

    public int registerPlayer(String name){
        int playerNumber = playerNames.size();
        playerNames.add(name);
        return playerNumber;
    }

    public int getOpponentNumber(int playerNr){
        if(playerNr == 0){
            return 1;
        }
        return 0;
    }
}
