package bg.sofia.uni.fmi.mjt.battleships;

import bg.sofia.uni.fmi.mjt.battleships.server.ClientHandler;
import bg.sofia.uni.fmi.mjt.battleships.server.Game;
import bg.sofia.uni.fmi.mjt.battleships.server.Player;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;

public class ClientHandlerTest {

    private List<ClientHandler> onlineClients = new CopyOnWriteArrayList<>();
    private List<Game> games = new CopyOnWriteArrayList<>();
    private ClientHandler clientHandler = new ClientHandler(new Socket(), onlineClients, games);
    private PrintWriter out = new PrintWriter(System.out);
    private BufferedReader in = new BufferedReader(new StringReader(""));

    @Test
    public void testRegisterClient() {
        List<ClientHandler> expectedOnlineClients = new CopyOnWriteArrayList<>();
        expectedOnlineClients.add(new ClientHandler(new Socket(), expectedOnlineClients, games));
        expectedOnlineClients.get(0).setClientName("Moni");

        List<ClientHandler> actualOnlineClients = new CopyOnWriteArrayList<>();
        ClientHandler testClientHandler = new ClientHandler(new Socket(), actualOnlineClients, games);
        actualOnlineClients.add(testClientHandler);
        testClientHandler.setOut(out);

        testClientHandler.registerClient("Moni");

        assertEquals(expectedOnlineClients.get(0).getClientName(),
                actualOnlineClients.get(0).getClientName());
    }

    @Test
    public void testCreateGame() {
        List<Game> expectedGames = new CopyOnWriteArrayList<>();
        expectedGames.add(new Game("myGame", "Maria"));

        List<Game> actualGames = new CopyOnWriteArrayList<>();
        ClientHandler testClientHandler = new ClientHandler(new Socket(), onlineClients, actualGames);
        onlineClients.add(testClientHandler);
        testClientHandler.setClientName("Maria");
        testClientHandler.setOut(out);

        testClientHandler.createGame("myGame");

        assertEquals(expectedGames.get(0).getGameName(),
                actualGames.get(0).getGameName());
    }

    @Test
    public void testJoinGame() {
        List<Game> actualGames = new CopyOnWriteArrayList<>();
        ClientHandler testClientHandler = new ClientHandler(new Socket(), onlineClients, actualGames);
        onlineClients.add(testClientHandler);
        testClientHandler.setClientName("Maria");
        testClientHandler.setOut(out);

        testClientHandler.createGame("myGame");
        testClientHandler.joinGame("myGame");

        assertEquals("Maria", actualGames.get(0).getPlayerOne().getName());
    }

    @Test
    public void testSaveGame() {
        Game game = new Game("myGame", "Maria");
        game.addPlayerOne(new Player("Maria", new Socket(), in, out));
        game.addPlayerTwo(new Player("Dani", new Socket(), in, out));

        ClientHandler testClientHandler = new ClientHandler(new Socket(), onlineClients, games);

        testClientHandler.setActiveGame(game);

        testClientHandler.saveGame();

        assertEquals(game.getGameName(),
                testClientHandler.getSavedGames().get(game).getSavedGameName());
    }
}
