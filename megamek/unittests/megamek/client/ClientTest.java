package megamek.client;
import junit.framework.TestCase;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import megamek.client.ui.swing.util.PlayerColour;
import megamek.common.*;
import megamek.common.event.GamePlayerChangeEvent;
import megamek.common.icons.Camouflage;
import megamek.common.net.Packet;
import megamek.common.options.GameOptions;

import megamek.common.options.OptionsConstants;
import megamek.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.io.File;
import java.util.Vector;

@RunWith(JUnit4.class)
public class ClientTest {
    @Mock
    private Game mockGame = Mockito.spy(Game.class);

    @InjectMocks
    private Server server;
    Server makeServer() throws IOException {
        return new Server("test", 1235, false, "");
    }
    @Test
    public void testCreateEndOfGamePacket() throws IOException{
        Server server = makeServer();
        server.setGame(mockGame);

        try {
            // prepare packet
            Class<?>[] methodArgumentTypes = null; // {};
            Object[] methodArguments = null; // new Object[0];
            Method method = server.getClass().getDeclaredMethod("createEndOfGamePacket",
                    methodArgumentTypes);
            method.setAccessible(true);

            Mockito.when(mockGame.getVictoryPlayerId()).thenReturn(1);
            Mockito.when(mockGame.getVictoryTeam()).thenReturn(3);
            Player player1 = new Player(1, "player1");
            Vector<IPlayer> players = new Vector<>();
            players.add(player1);
            Mockito.when(mockGame.getPlayersVector()).thenReturn(players);
            player1.setPlayerRating(5);

            Packet packet = (Packet) method.invoke(server, methodArguments);

            player1.setPlayerRating(7);
            TestCase.assertEquals(7, player1.getPlayerRating());

            Client client = new Client("client", "localhost", 1235);
            client.game = mockGame;
            client.handlePacket(packet);

            TestCase.assertEquals(3, mockGame.getVictoryTeam());
            TestCase.assertEquals(1, mockGame.getVictoryPlayerId());
            TestCase.assertEquals(5, player1.getPlayerRating());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}