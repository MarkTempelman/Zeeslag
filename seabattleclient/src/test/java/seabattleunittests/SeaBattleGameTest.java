/*
 * Sea Battle Start project.
 */
package seabattleunittests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seabattlegame.ISeaBattleGame;
import seabattlegame.SeaBattleGame;
import seabattlegui.ShipType;
import seabattlegui.ShotType;
import seabattlegui.SquareState;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for Sea Battle game.
 * @author Nico Kuijpers
 */
class SeaBattleGameTest {
    
    private ISeaBattleGame game;
    private MockSeaBattleApplication applicationPlayer;
    private MockSeaBattleApplication applicationOpponent;
    
    SeaBattleGameTest() {
    }

    @BeforeEach
    void setUp() {
        
        // Create the Sea Battle game
        game = new SeaBattleGame();
        
        // Create mock Sea Battle GUI for player
        applicationPlayer = new MockSeaBattleApplication();
        
        // Create mock Sea Battle GUI for opponent
        applicationOpponent = new MockSeaBattleApplication();
    }
    
    @AfterEach
    void tearDown() {
    }

    /**
     * Example test for method registerPlayerName(). 
     * Test whether an IllegalArgumentException is thrown when parameter 
     * name is null.
     * @author Nico Kuijpers
     */
    @Test() // expected=IllegalArgumentException.class
    void testRegisterPlayerNameNull() {


        // Register player with parameter name null in single-player mode
        String name = null;
        String password = "password";
        boolean singlePlayerMode = true;

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> game.registerPlayer(name, password, applicationPlayer, singlePlayerMode),
                "Expected registerPlayer() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Username"));
    }

    @Test()
    void testRegisterPlayerPasswordNull() {
        String name = "name";
        String password = "";
        boolean singlePlayerMode = true;

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> game.registerPlayer(name, password, applicationPlayer, singlePlayerMode),
                "Expected registerPlayer() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("Password"));
    }

    @Test()
    void testRegisterPlayerApplicationNull(){
        String name = "name";
        String password = "password";
        boolean singlePlayerMode = true;

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> game.registerPlayer(name, password, null, singlePlayerMode),
                "Expected registerPlayer() to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("application"));
    }
    
    /**
     * Example test for method placeShipsAutomatically().
     * Test whether the correct number of squares contain a ship in the
     * ocean area of the player's application.
     */
    @Test
    void testPlaceShipsAutomatically() {
        
        // Register player in single-player mode
        game.registerPlayer("Some Name", "Some Password", applicationPlayer, true);
        
        // Place ships automatically
        int playerNr = applicationPlayer.getPlayerNumber();
        game.placeShipsAutomatically(playerNr);
        
        // Count number of squares where ships are placed in player's application
        int expectedResult = 5 + 4 + 3 + 3 + 2;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);
        assertEquals(expectedResult,actualResult, "Wrong number of squares where ships are placed");
    }

    @Test
    void testPlaceShip(){
        game.registerPlayer("s", "s", applicationPlayer, true);

        game.placeShip(0, ShipType.CRUISER, 4, 4, true);


        int expectedResult = 3;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);

        assertEquals(expectedResult,actualResult, "Wrong number of squares where ships are placed");
    }

    @Test
    void placeShipOutOfBounds(){
        game.placeShip(0, ShipType.CRUISER, 9, 9, true);

        int expectedResult = 0;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);

        assertEquals(expectedResult,actualResult, "Wrong number of squares where ships are placed");
    }

    @Test
    void testPlaceShipVertical(){
        game.placeShip(0, ShipType.CRUISER, 4, 4, false);

        int expectedResult = 3;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);

        assertEquals(expectedResult,actualResult, "Wrong number of squares where ships are placed");
    }

    @Test
    void testRemoveShip(){
        game.placeShip(0, ShipType.CRUISER, 4, 4, true);
        game.removeShip(0, 4, 4);

        int expectedResult = 0;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);

        assertEquals(expectedResult,actualResult, "There are still ships.");
    }

    @Test
    void testRemoveShipNoShip(){
        game.placeShip(0, ShipType.CRUISER, 4, 4, true);
        game.removeShip(0, 9, 9);

        int expectedResult = 3;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);

        assertEquals(expectedResult,actualResult, "There are still ships.");
    }

    @Test
    void testRemoveAllShips(){
        game.placeShip(0, ShipType.CRUISER, 4, 4, true);
        game.placeShip(0, ShipType.MINESWEEPER, 2, 4, true);
        game.removeAllShips(0);

        int expectedResult = 0;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);

        assertEquals(expectedResult,actualResult, "There are still ships.");
    }

    @Test
    void testNotifyWhenReady(){
        game.notifyWhenReady(0);
        String error = applicationPlayer.getErrorMessage();
        String expected = "Not all ships have been placed!";
        assertEquals(expected,error, "This isn't right");
    }

    @Test
    void testNotifyWhenReadySuccess(){
        game.registerPlayer("Some Name", "Some Password", applicationPlayer, true);
        int playerNr = applicationPlayer.getPlayerNumber();
        game.placeShipsAutomatically(playerNr);

        game.notifyWhenReady(playerNr);
        String error = applicationPlayer.getErrorMessage();
        String expected = null;

        assertEquals(expected,error, "This isn't right");
    }

    @Test
    void testFireShot(){
        game.fireShot(0, 3, 3);
        ShotType t = applicationPlayer.getLastShotPlayer();
        ShotType expected = ShotType.MISSED;
        assertEquals(expected,t, "It was a different ShotType.");
    }

    @Test
    void testFireShotHit(){
        game.placeShip(0, ShipType.CRUISER, 4, 4, true);

        game.fireShot(0, 4, 4);
        ShotType t = applicationPlayer.getLastShotPlayer();
        ShotType expected = ShotType.HIT;
        assertEquals(expected,t, "It was a different ShotType.");
    }

    @Test
    void testStartNewGameFail(){
        game.placeShip(0, ShipType.CRUISER, 4, 4, true);
        game.placeShip(0, ShipType.MINESWEEPER, 2, 4, true);

        game.startNewGame(0);
        int expectedResult = 0;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);
        assertEquals(expectedResult,actualResult, "There are still ships, so the board hasn't been cleared.");
    }

    @Test
    void testStartNewGameSuccess(){
        game.placeShipsAutomatically(0);

        game.startNewGame(0);
        int expectedResult = 5 + 4 + 3 + 3 + 2;
        int actualResult = applicationPlayer.numberSquaresPlayerWithSquareState(SquareState.SHIP);
        assertEquals(expectedResult,actualResult, "There are still ships, so the board hasn't been cleared.");
    }
}

