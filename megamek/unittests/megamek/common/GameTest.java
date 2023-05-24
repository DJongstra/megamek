package megamek.common;
import junit.framework.TestCase;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import megamek.client.ui.swing.util.PlayerColour;
import megamek.common.Entity;
import megamek.common.MechFileParser;
import megamek.common.Minefield;
import megamek.common.event.GamePlayerChangeEvent;
import megamek.common.icons.Camouflage;
import megamek.common.options.GameOptions;

import megamek.common.options.OptionsConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.*;
import java.io.File;

@RunWith(JUnit4.class)
public class GameTest {
    Game game = new Game();

    @Test
    public void testGetPlayerRatings(){
        Player player1 = new Player(1, "player1");
        player1.setPlayerRating(3);
        Player player2 = new Player(2, "player2");
        player2.setPlayerRating(5);

        game.addPlayer(1, player1);
        game.addPlayer(2, player2);

        Map<Integer, Integer> ratings = game.getPlayerRatings();
        TestCase.assertEquals(2, ratings.size());
        TestCase.assertEquals(3, (int) ratings.get(1));
        TestCase.assertEquals(5, (int) ratings.get(2));
    }

    @Test
    public void testEnd() {
        Player player1 = new Player(1, "player1");
        player1.setPlayerRating(3);
        Player player2 = new Player(2, "player2");
        player2.setPlayerRating(5);

        game.addPlayer(1, player1);
        game.addPlayer(2, player2);

        Map<Integer, Integer> ratings = game.getPlayerRatings();
        TestCase.assertEquals(2, ratings.size());
        TestCase.assertEquals(3, (int) ratings.get(1));
        TestCase.assertEquals(5, (int) ratings.get(2));

        Map<Integer, Integer> newRatings = new HashMap<>();
        newRatings.put(1, 6);
        newRatings.put(2, 8);
        game.end(1, 3, newRatings );
        TestCase.assertEquals(3, game.getVictoryTeam());
        TestCase.assertEquals(1, game.getVictoryPlayerId());

        Map<Integer, Integer> updatedRatings = game.getPlayerRatings();
        TestCase.assertEquals(2, ratings.size());
        TestCase.assertEquals(6, (int) updatedRatings.get(1));
        TestCase.assertEquals(8, (int) updatedRatings.get(2));
    }

}
