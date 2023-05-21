package megamek.server;
import junit.framework.TestCase;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import megamek.client.ui.swing.util.PlayerColour;
import megamek.common.Entity;
import megamek.common.Game;
import megamek.common.MechFileParser;
import megamek.common.Minefield;
import megamek.common.event.GamePlayerChangeEvent;
import megamek.common.icons.Camouflage;
import megamek.common.net.Packet;
import megamek.common.options.GameOptions;

import megamek.common.options.OptionsConstants;
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
public class ServerTest {
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
            Class<?>[] methodArgumentTypes = null; // {};
            Object[] methodArguments = null; // new Object[0];
            Method method = server.getClass().getDeclaredMethod("createEndOfGamePacket",
                    methodArgumentTypes);
            method.setAccessible(true);

            Mockito.when(mockGame.getVictoryPlayerId()).thenReturn(1);
            Mockito.when(mockGame.getVictoryTeam()).thenReturn(3);

            Packet packet = (Packet) method.invoke(server, methodArguments);
            TestCase.assertEquals(380, packet.getCommand());
            TestCase.assertEquals(1, packet.getData()[1]);
            TestCase.assertEquals(3, packet.getData()[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
