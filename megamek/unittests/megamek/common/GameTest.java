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

    private GameTurn getGameturn(Entity entity){
        if (entity instanceof Infantry) {
            return new GameTurn.EntityClassTurn(1, 1);
        } else if (entity instanceof Protomech) {
            return new GameTurn.EntityClassTurn(1, 2);
        } else if (entity instanceof Tank) {
            return new GameTurn.EntityClassTurn(1, 4);
        } else if (entity instanceof Mech) {
            return new GameTurn.EntityClassTurn(1, 8);
        } else {
            return new GameTurn.EntityClassTurn(1, 3);

        }
    }
    @Test
    public void testRemoveTurnFor() {
        Game game = Mockito.spy(Game.class);
        List<GameTurn> gameTurns = new LinkedList<>();
        GameTurn gameTurn1 = Mockito.mock(GameTurn.class);
        Mockito.when(gameTurn1.isValidEntity(Mockito.any(Entity.class), Mockito.any(Game.class), Mockito.anyBoolean())).thenReturn(false);

        GameTurn mockEntityGameTurn = Mockito.mock(GameTurn.EntityClassTurn.class); //new GameTurn.EntityClassTurn(1, 1);
        gameTurns.add(gameTurn1);
        gameTurns.add(mockEntityGameTurn);
        game.setTurnVector(gameTurns);
        GameOptions gameOptions = Mockito.mock(GameOptions.class);
        Mockito.when(game.getOptions()).thenReturn(gameOptions);

        // mocks for 'move later' check
        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_INF_MOVE_LATER)).thenReturn(true);
        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_PROTOS_MOVE_LATER)).thenReturn(true);

        // mock entities
        Infantry mockInfantry = Mockito.spy(Infantry.class);
        Protomech mockProtomech = Mockito.spy(Protomech.class);
        Mech mockMech = Mockito.spy(TripodMech.class);

        // entities to check
        Vector<Entity> entities = new Vector<>();
        entities.add(mockInfantry);
        entities.add(mockProtomech);
        entities.add(mockMech);

        Mockito.when(mockInfantry.getOwnerId()).thenReturn(1);

        // check 'move later' optional rules
        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_INF_MOVE_MULTI)).thenReturn(false);
        Mockito.when(mockEntityGameTurn.isValidEntity(Mockito.any(Entity.class), Mockito.any(Game.class), Mockito.eq(false))).thenReturn(true);
        Mockito.when(mockEntityGameTurn.isValidEntity(Mockito.any(Entity.class), Mockito.any(Game.class), Mockito.eq(true))).thenReturn(false);
        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_INF_MOVE_LATER)).thenReturn(true);
        game.removeTurnFor(mockInfantry);
        TestCase.assertEquals(1, game.getTurnVector().size());

        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_INF_MOVE_LATER)).thenReturn(false);
        game.setTurnVector(gameTurns);
        game.removeTurnFor(mockInfantry);
        TestCase.assertEquals(2, game.getTurnVector().size());

        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_PROTOS_MOVE_LATER)).thenReturn(false);
        game.setTurnVector(gameTurns);
        game.removeTurnFor(mockProtomech);
        TestCase.assertEquals(2, game.getTurnVector().size());

        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_PROTOS_MOVE_LATER)).thenReturn(true);
        game.setTurnVector(gameTurns);
        game.removeTurnFor(mockProtomech);
        TestCase.assertEquals(1, game.getTurnVector().size());

        //mock everything for the multi move possibilities
        Vector<IGame.Phase> phases = new Vector<>();
        phases.add(IGame.Phase.PHASE_MOVEMENT);
        phases.add(IGame.Phase.PHASE_INITIATIVE);

        // add to entities to check
        Tank mockTank = Mockito.spy(Tank.class);
        entities.add(mockTank);
        Dropship mockDropship = Mockito.spy(Dropship.class);
        entities.add(mockDropship);

        // mock options for infantry
        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_INF_MOVE_MULTI)).thenReturn(true);
        Mockito.when(gameOptions.intOption(OptionsConstants.INIT_INF_PROTO_MOVE_MULTI)).thenReturn(1);

        // mock options for protomechs
        Mockito.when(gameOptions.booleanOption(OptionsConstants.INIT_PROTOS_MOVE_MULTI)).thenReturn(true);

        // mock options for mechs
        Mockito.when(gameOptions.booleanOption(OptionsConstants.ADVGRNDMOV_MEK_LANCE_MOVEMENT)).thenReturn(true);
        Mockito.when(gameOptions.intOption(OptionsConstants.ADVGRNDMOV_MEK_LANCE_MOVEMENT_NUMBER)).thenReturn(1);

        // mock options for vehicles
        Mockito.when(gameOptions.booleanOption(OptionsConstants.ADVGRNDMOV_VEHICLE_LANCE_MOVEMENT)).thenReturn(true);
        Mockito.when(gameOptions.intOption(OptionsConstants.ADVGRNDMOV_VEHICLE_LANCE_MOVEMENT_NUMBER)).thenReturn(1);

        // ensure that turn is only removed in first part before the boolean check 'useInfantryMoveLater'
        Mockito.when(mockEntityGameTurn.isValidEntity(Mockito.any(Entity.class), Mockito.any(Game.class), Mockito.anyBoolean())).thenReturn(false);


        // check all entities in a phase that would leave the turn at the entity and one that would not
        for (IGame.Phase phase: phases) {
            for (Entity entity : entities) {
                // determine whether to mock the gameTurn object
                if (phase == IGame.Phase.PHASE_MOVEMENT) {
                    gameTurns.set(1, getGameturn(entity));
                } else {
                    gameTurns.set(1, mockEntityGameTurn);
                }
                game.setTurnVector(gameTurns);

                game.setPhase(phase);
                Mockito.when((game.getInfantryLeft(1))).thenReturn(1);

                game.removeTurnFor(entity);

                if (phase == IGame.Phase.PHASE_MOVEMENT && !(entity instanceof Dropship)) {
                    TestCase.assertEquals(1, game.getTurnVector().size());
                } else {
                    TestCase.assertEquals(2, game.getTurnVector().size());
                }
            }

        }


    }

}
