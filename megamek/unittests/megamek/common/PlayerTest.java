/*
 * MegaMek - Copyright (C) 2000,2001,2002,2003,2004,2005 Ben Mazur
 * (bmazur@sev.org)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */
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

import java.util.ArrayList;
import java.io.File;
import java.util.Vector;

@RunWith(JUnit4.class)
public class PlayerTest {
    private Player player;
    private Game game;
    private Dropship mockDropship;

    @Before
    public void setup(){
        game = Mockito.mock(Game.class);
        player = new Player(1, "test_player");
        mockDropship = Mockito.mock(Dropship.class);
        game.addEntity(mockDropship);

        player.setGame(game);

    }

    @Test
    public void testMinefields() {
        // get initial minefields
        Vector<Minefield> initialMinefields = player.getMinefields();
        TestCase.assertEquals(0, initialMinefields.size());

        // add a single minefield
        Minefield minefield1 = Minefield.createMinefield(new Coords(0,0), 1, 1, 1);
        player.addMinefield(minefield1);
        Vector<Minefield> minefields1 = player.getMinefields();
        TestCase.assertEquals(1, minefields1.size());
        TestCase.assertEquals(minefield1, minefields1.get(0));

        // add multiple minefields
        Vector<Minefield> minefieldsToAdd = new Vector<>();
        Minefield minefield2 = Minefield.createMinefield(new Coords(1,0),1 ,1 ,1);
        Minefield minefield3 = Minefield.createMinefield(new Coords(2,0),1 ,1 ,1);
        minefieldsToAdd.add(minefield2);
        minefieldsToAdd.add(minefield3);
        player.addMinefields(minefieldsToAdd);
        Vector<Minefield> minefields2 = player.getMinefields();
        TestCase.assertEquals(3, minefields2.size());
        TestCase.assertEquals(minefield1, minefields2.get(0));
        TestCase.assertEquals(minefield2, minefields2.get(1));
        TestCase.assertEquals(minefield3, minefields2.get(2));

        // remove minefield
        player.removeMinefield(minefield2);
        Vector<Minefield> minefields3 = player.getMinefields();
        TestCase.assertEquals(2, minefields3.size());
        TestCase.assertEquals(minefield1, minefields3.get(0));
        TestCase.assertEquals(minefield3, minefields3.get(1));

        // contains minefield
        TestCase.assertTrue(player.containsMinefield(minefield1));
        TestCase.assertFalse(player.containsMinefield(minefield2));
        TestCase.assertTrue(player.containsMinefield(minefield3));

        // remove all minefields
        player.removeMinefields();
        Vector<Minefield> minefields4 = player.getMinefields();
        TestCase.assertEquals(0, minefields4.size());
    }

    @Test
    public void testMinefieldNumbers() {
        // initial minefield numbers
        TestCase.assertEquals(0,player.getNbrMFActive());
        TestCase.assertEquals(0, player.getNbrMFCommand());
        TestCase.assertEquals(0, player.getNbrMFConventional());
        TestCase.assertEquals(0, player.getNbrMFInferno());
        TestCase.assertEquals(0, player.getNbrMFVibra());

        // active
        player.setNbrMFActive(1);
        TestCase.assertEquals(1,player.getNbrMFActive());
        TestCase.assertTrue(player.hasMinefields());
        player.setNbrMFActive(0);

        // command
        player.setNbrMFCommand(1);
        TestCase.assertEquals(1,player.getNbrMFCommand());
        TestCase.assertTrue(player.hasMinefields());
        player.setNbrMFCommand(0);

        // conventional
        player.setNbrMFConventional(1);
        TestCase.assertEquals(1,player.getNbrMFConventional());
        TestCase.assertTrue(player.hasMinefields());
        player.setNbrMFConventional(0);

        // inferno
        player.setNbrMFInferno(1);
        TestCase.assertEquals(1,player.getNbrMFInferno());
        TestCase.assertTrue(player.hasMinefields());
        player.setNbrMFInferno(0);

        // vibra
        player.setNbrMFVibra(1);
        TestCase.assertEquals(1,player.getNbrMFVibra());
        TestCase.assertTrue(player.hasMinefields());
        player.setNbrMFVibra(0);
    }

    @Test
    public void testCamo() {
        // initial
        TestCase.assertEquals(Camouflage.COLOUR_CAMOUFLAGE, player.getCamoCategory());
        TestCase.assertEquals(PlayerColour.BLUE.name(), player.getCamoFileName());
        Camouflage initialCamouflage = player.getCamouflage();
        Camouflage expectedCamouflage = new Camouflage(Camouflage.COLOUR_CAMOUFLAGE, PlayerColour.BLUE.name());
        TestCase.assertEquals(expectedCamouflage, initialCamouflage);

        // camo category
        player.setCamoCategory(Camouflage.NO_CAMOUFLAGE);
        Camouflage expectedCamouflage2 = new Camouflage(Camouflage.NO_CAMOUFLAGE, PlayerColour.BLUE.name());
        TestCase.assertEquals(expectedCamouflage2, player.getCamouflage());

        // camo file
        player.setCamoFileName(PlayerColour.CYAN.name());
        Camouflage expectedCamouflage3 = new Camouflage(Camouflage.NO_CAMOUFLAGE, PlayerColour.CYAN.name());
        TestCase.assertEquals(expectedCamouflage3, player.getCamouflage());
    }

    @Test
    public void testPlayerInfo(){
        // name
        TestCase.assertEquals("test_player", player.getName());
        player.setName("testPlayer");
        TestCase.assertEquals("testPlayer", player.getName());

        // id
        TestCase.assertEquals(1, player.getId());

        // team
        TestCase.assertEquals(0, player.getTeam());
        player.setTeam(2);
        TestCase.assertEquals(2, player.getTeam());

        // hash code
        TestCase.assertEquals(1, player.hashCode());

        // player string
        TestCase.assertEquals("Player 1 (testPlayer)", player.toString());
    }

    @Test
    public void testSetDone(){
        TestCase.assertFalse(player.isDone());

        player.setDone(true);

        Mockito.verify(game).processGameEvent(Mockito.any(GamePlayerChangeEvent.class));
        TestCase.assertTrue(player.isDone());
    }

    @Test
    public void testSetGhost(){
        TestCase.assertFalse(player.isGhost());
        player.setGhost(true);
        TestCase.assertTrue(player.isGhost());
        player.setGhost(false);
    }

    @Test
    public void testSetObserver(){
        TestCase.assertFalse(player.isObserver());
        TestCase.assertFalse(player.getSeeAll());

        player.setSeeAll(true);
        TestCase.assertTrue(player.getSeeAll());
        TestCase.assertFalse(player.canSeeAll());

        player.setObserver(true);
        TestCase.assertTrue(player.canSeeAll());
        TestCase.assertTrue(player.isObserver());

        Mockito.when(game.getTeamForPlayer(Mockito.any(Player.class))).thenReturn(new Team(2));
        player.setObserver(false);
        TestCase.assertFalse(player.isObserver());
        TestCase.assertFalse(player.canSeeAll());
        TestCase.assertFalse(player.getSeeAll());

        Mockito.when(game.getPhase()).thenReturn(IGame.Phase.PHASE_VICTORY);
        TestCase.assertFalse(player.isObserver());
    }

    @Test
    public void testSetColour(){
        TestCase.assertEquals(PlayerColour.BLUE, player.getColour());
        player.setColour(PlayerColour.CORAL);
        TestCase.assertEquals(PlayerColour.CORAL, player.getColour());
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            player.setColour(null);
        });
        String actualMessage = exception.getMessage();
        TestCase.assertTrue(actualMessage.contains("Colour cannot be set to null"));
    }

    @Test
    public void testStartingPosition(){
        TestCase.assertEquals(Board.START_ANY, player.getStartingPos());
        player.setStartingPos(20);
        TestCase.assertEquals(20, player.getStartingPos());
        player.adjustStartingPosForReinforcements();
        TestCase.assertEquals(Board.START_ANY, player.getStartingPos());
        player.setStartingPos(9);
        player.adjustStartingPosForReinforcements();
        TestCase.assertEquals(9, player.getStartingPos());
    }

    @Test
    public void testIsEnemyOf(){
        // enemy
        Player enemyPlayer = new Player(2, "enemyPlayer");
        enemyPlayer.setTeam(3);
        TestCase.assertTrue(player.isEnemyOf(enemyPlayer));

        // null
        TestCase.assertTrue(player.isEnemyOf(null));

        // player's team = TEAM_NONE & enemy's team = TEAM_NONE
        player.setTeam(IPlayer.TEAM_NONE);
        enemyPlayer.setTeam(IPlayer.TEAM_NONE);
        assertTrue(player.isEnemyOf(enemyPlayer));

        // both teams are TEAM_UNASSIGNED
        player.setTeam(IPlayer.TEAM_UNASSIGNED);
        enemyPlayer.setTeam(IPlayer.TEAM_UNASSIGNED);
        TestCase.assertTrue(player.isEnemyOf(enemyPlayer));

        // no enemies
        Player friendlyPlayer = new Player(3, "friendlyPlayer");
        friendlyPlayer.setTeam(3);
        player.setTeam(3);
        TestCase.assertFalse(player.isEnemyOf(friendlyPlayer));
    }

    @Test
    public void testEquals(){
        // same player object
        TestCase.assertTrue(player.equals(player));

        // null comparison
        TestCase.assertFalse(player.equals(null));

        // different class comparison
        Board board = new Board();
        TestCase.assertFalse(player.equals(board));

        // player different id
        Player differentPlayer = new Player(2, "differentPlayer");
        TestCase.assertFalse(player.equals(differentPlayer));

        // player same id
        Player samePlayer = new Player(1, "samePlayer");
        TestCase.assertTrue(player.equals(samePlayer));
    }

    @Test
    public void testAdmitDefeat() {
        TestCase.assertFalse(player.admitsDefeat());
        player.setAdmitsDefeat(true);
        TestCase.assertTrue(player.admitsDefeat());
        player.setAdmitsDefeat(false);
        TestCase.assertFalse(player.admitsDefeat());
    }

    @Test
    public void testAllowTeamChange(){
        TestCase.assertFalse(player.isAllowingTeamChange());
        player.setAllowTeamChange(true);
        TestCase.assertTrue(player.isAllowingTeamChange());
        player.setAllowTeamChange(false);
        TestCase.assertFalse(player.isAllowingTeamChange());
    }

    @Test
    public void testArtyAutoHitHexes(){
        Coords coord1 = new Coords(0,0);
        Coords coord2 = new Coords(1,1);
        Coords coord3 = new Coords(2,2);

        // initial hexes
        TestCase.assertEquals(0, player.getArtyAutoHitHexes().size());

        // set multiple
        Vector<Coords> hexesToSet = new Vector<>();
        hexesToSet.add(coord1);
        hexesToSet.add(coord2);
        player.setArtyAutoHitHexes(hexesToSet);
        Vector<Coords> result1 = player.getArtyAutoHitHexes();
        TestCase.assertEquals(2, result1.size());
        TestCase.assertEquals(hexesToSet, result1);

        // add single coord
        player.addArtyAutoHitHex(coord3);
        Vector<Coords> result2 = player.getArtyAutoHitHexes();
        TestCase.assertEquals(3, result2.size());
        TestCase.assertEquals(coord1, result2.get(0));
        TestCase.assertEquals(coord2, result2.get(1));
        TestCase.assertEquals(coord3, result2.get(2));

        // remove all
        player.removeArtyAutoHitHexes();
        TestCase.assertEquals(0, player.getArtyAutoHitHexes().size());
    }

    @Test
    public void testHasTag(){
        Player player1 = new Player(1, "player1");
        Game game = new Game();
        Dropship dropship = new Dropship();

        game.addPlayer(1, player1);
        game.addEntity(dropship);
        TestCase.assertFalse(player1.hasTAG());

        dropship.setOwner(player1);
        game.addEntity(dropship);
        TestCase.assertFalse(player1.hasTAG());

        Dropship mockShip = Mockito.mock(Dropship.class);
        Mockito.when(mockShip.hasTAG()).thenReturn(true);
        Mockito.when(mockShip.getOwner()).thenReturn(player1);
        game.addEntity(mockShip);
        TestCase.assertTrue(player1.hasTAG());

        Player player2 = new Player(2, "player2");
        game.addPlayer(2, player2);
        TestCase.assertFalse(player2.hasTAG());
    }

    private void resetMockDropship() {
        Mockito.when(mockDropship.getOwner()).thenReturn(null);
    }

    @Test
    public void testGetBV() {
        Game game1 = new Game();
        Player player1 = new Player(1, "player1");
        game1.addPlayer(1, player1);
        Mockito.when(mockDropship.calculateBattleValue()).thenReturn(10);

        TestCase.assertEquals(0, player1.getBV());

        game1.addEntity(mockDropship);
        TestCase.assertEquals(0, player1.getBV());
        player1.setInitialBV();
        TestCase.assertEquals(0, player1.getInitialBV());

        Mockito.when(mockDropship.getOwner()).thenReturn(player1);
        TestCase.assertEquals(10, player1.getBV());
        player1.setInitialBV();
        TestCase.assertEquals(10, player1.getInitialBV());

        Mockito.when(mockDropship.isTrapped()).thenReturn(true);
        TestCase.assertEquals(0, player1.getBV());

        Mockito.when(mockDropship.isDestroyed()).thenReturn(true);
        TestCase.assertEquals(0, player1.getBV());
    }

    @Test
    public void testGetFledBV(){
        Vector<Entity> fled = new Vector<>();
        Mockito.when(mockDropship.calculateBattleValue()).thenReturn(10);
        Mockito.when(game.getRetreatedEntities()).thenReturn(fled.elements());

        TestCase.assertEquals(0, player.getFledBV());

        fled.add(mockDropship);
        Mockito.when(game.getRetreatedEntities()).thenReturn(fled.elements());
        Mockito.when((mockDropship.getOwner())).thenReturn(player);
        TestCase.assertEquals(10, player.getFledBV());

        Mockito.when((mockDropship.getOwner())).thenReturn(null);
        TestCase.assertEquals(0, player.getFledBV());
    }

    @Test
    public void testSetInitialBV(){
        TestCase.assertEquals(0, player.getInitialBV());

        player.increaseInitialBV(2);
        TestCase.assertEquals(2, player.getInitialBV());
    }

    @Test
    public void testSetInitCompensationBonus(){
        TestCase.assertEquals(0, player.getInitCompensationBonus());

        player.setInitCompensationBonus(3);
        TestCase.assertEquals(3, player.getInitCompensationBonus());
    }

    @Test
    public void testSetConstantInitBonus(){
        TestCase.assertEquals(0, player.getConstantInitBonus());

        player.setConstantInitBonus(2);
        TestCase.assertEquals(2, player.getConstantInitBonus());
    }

    @Test
    public void testGetTurnInitBonus() {
        // no game for player
        Player player1 = new Player(2, "player1");
        TestCase.assertEquals(0, player1.getTurnInitBonus());

        // no entity vector for player's game
        Mockito.when(game.getEntitiesVector()).thenReturn(null);
        TestCase.assertEquals(0, player.getTurnInitBonus());

        // entity not owned by player to calculate the bonus for
        Vector<Entity> entities = new Vector<>();
        Mockito.when(mockDropship.getOwner()).thenReturn(player1);
        entities.add(mockDropship);
        Mockito.when(game.getEntitiesVector()).thenReturn(entities);
        TestCase.assertEquals(0, player.getTurnInitBonus());

        // player's entity, but game option is false
        Mockito.when(mockDropship.getOwner()).thenReturn(player);
        GameOptions gameOptions = Mockito.mock(GameOptions.class);
        Mockito.when(gameOptions.booleanOption(OptionsConstants.ADVANCED_TACOPS_MOBILE_HQS)).thenReturn(false);
        Mockito.when(game.getOptions()).thenReturn(gameOptions);
        TestCase.assertEquals(0, player.getTurnInitBonus());

        // entity's HQ and Quirk bonus is 0
        Mockito.when(gameOptions.booleanOption(OptionsConstants.ADVANCED_TACOPS_MOBILE_HQS)).thenReturn(true);
        Mockito.when(game.getOptions()).thenReturn(gameOptions);
        Mockito.when(mockDropship.getHQIniBonus()).thenReturn(0);
        Mockito.when(mockDropship.getQuirkIniBonus()).thenReturn(0);
        TestCase.assertEquals(0, player.getTurnInitBonus());

        // non-zero HQ bonus
        Mockito.when(mockDropship.getHQIniBonus()).thenReturn(1);
        TestCase.assertEquals(1, player.getTurnInitBonus());

        // non-zero Quirk bonus
        Mockito.when(mockDropship.getQuirkIniBonus()).thenReturn(2);
        TestCase.assertEquals(3, player.getTurnInitBonus());

        // 2 entities
        ArmlessMech mech = Mockito.mock(ArmlessMech.class);
        Mockito.when(mech.getOwner()).thenReturn(player);
        Mockito.when(mech.getHQIniBonus()).thenReturn(6);
        Mockito.when(mech.getQuirkIniBonus()).thenReturn(0);
        TestCase.assertEquals(3, player.getTurnInitBonus());
    }

    @Test
    public void testGetCommandBonus(){
        GameOptions gameOptions = Mockito.mock(GameOptions.class);

        // no game for player
        Player player1 = new Player(2, "player1");
        TestCase.assertEquals(0, player1.getCommandBonus());

        // entity not owned by a player
        Vector<Entity> entities = new Vector<>();
        Mockito.when(mockDropship.getOwner()).thenReturn(null);
        entities.add(mockDropship);
        Mockito.when(game.getEntitiesVector()).thenReturn(entities);
        TestCase.assertEquals(0, player.getCommandBonus());

        // entity not owned by correct player
        Mockito.when(mockDropship.getOwner()).thenReturn(player1);
        entities.add(mockDropship);
        TestCase.assertEquals(0, player.getCommandBonus());

        // entity is destroyed
        Mockito.when(mockDropship.getOwner()).thenReturn(player);
        Mockito.when(mockDropship.isDestroyed()).thenReturn(true);
        TestCase.assertEquals(0, player.getCommandBonus());

        // entity not deployed
        Mockito.when(mockDropship.isDestroyed()).thenReturn(false);
        Mockito.when(mockDropship.isDeployed()).thenReturn(false);
        TestCase.assertEquals(0, player.getCommandBonus());

        // entity is off-board
        Mockito.when(mockDropship.isDeployed()).thenReturn(true);
        Mockito.when(mockDropship.isOffBoard()).thenReturn(true);
        TestCase.assertEquals(0, player.getCommandBonus());

        // entity does not have active crew
        Mockito.when(mockDropship.isOffBoard()).thenReturn(false);
        Crew crew = Mockito.mock(Crew.class);
        Mockito.when(mockDropship.getCrew()).thenReturn(crew);
        Mockito.when(crew.isActive()).thenReturn(false);
        TestCase.assertEquals(0, player.getCommandBonus());

        // entity is captured
        Mockito.when(crew.isActive()).thenReturn(true);
        Mockito.when(mockDropship.isCaptured()).thenReturn(true);
        TestCase.assertEquals(0, player.getCommandBonus());

        // game option not RPG, has command console bonus
        Mockito.when(mockDropship.isCaptured()).thenReturn(false);
        Mockito.when(game.getOptions()).thenReturn(gameOptions);
        Mockito.when(gameOptions.booleanOption(OptionsConstants.RPG_COMMAND_INIT)).thenReturn(false);
        Mockito.when(mockDropship.hasCommandConsoleBonus()).thenReturn(true);
        TestCase.assertEquals(2, player.getCommandBonus());

        // rpg option true
        Mockito.when(gameOptions.booleanOption(OptionsConstants.RPG_COMMAND_INIT)).thenReturn(true);
        Mockito.when(mockDropship.hasCommandConsoleBonus()).thenReturn(false);
        Mockito.when(crew.hasActiveTechOfficer()).thenReturn(true);
        Mockito.when(crew.getCommandBonus()).thenReturn(3);
        TestCase.assertEquals(5, player.getCommandBonus());

        // negative command bonus
        Mockito.when(crew.hasActiveTechOfficer()).thenReturn(false);
        Mockito.when(crew.getCommandBonus()).thenReturn(-1);
        TestCase.assertEquals(0, player.getCommandBonus());

        MechWarrior mechWarrior = Mockito.mock(MechWarrior.class);
        entities.add(mechWarrior);
        Mockito.when(mechWarrior.getOwner()).thenReturn(player);
        Mockito.when(mechWarrior.isDestroyed()).thenReturn(false);
        Mockito.when(mechWarrior.isDeployed()).thenReturn(true);
        Mockito.when(mechWarrior.isOffBoard()).thenReturn(false);
        Mockito.when(mechWarrior.getCrew()).thenReturn(crew);
        Mockito.when(mechWarrior.isCaptured()).thenReturn(false);
        TestCase.assertEquals(0, player.getCommandBonus());
    }

    @Test
    public void testGetAirborneVTOL() {
        Vector<Entity> entities = new Vector<>();
        Mockito.when(mockDropship.getOwner()).thenReturn(player);
        Mockito.when(mockDropship.getId()).thenReturn(3);
        entities.add(mockDropship);
        Mockito.when(game.getEntitiesVector()).thenReturn(entities);
        TestCase.assertEquals(0, player.getAirborneVTOL().size());

        VTOL vtol = Mockito.mock(VTOL.class);
        Mockito.when(vtol.getOwner()).thenReturn(player);
        Mockito.when(vtol.getId()).thenReturn(4);
        entities.add(vtol);
        Mockito.when(vtol.isDestroyed()).thenReturn(false);
        Mockito.when(vtol.getElevation()).thenReturn(1);
        Vector<Integer> ids1 = player.getAirborneVTOL();
        TestCase.assertEquals(1, ids1.size());
        TestCase.assertTrue(ids1.contains(4));

        Mockito.when(vtol.isDestroyed()).thenReturn(true);
        TestCase.assertEquals(0, player.getAirborneVTOL().size());

        Mockito.when(vtol.isDestroyed()).thenReturn(false);
        Mockito.when(vtol.getElevation()).thenReturn(0);
        TestCase.assertEquals(0, player.getAirborneVTOL().size());

        Mockito.when(vtol.getElevation()).thenReturn(1);
        Mockito.when(mockDropship.getMovementMode()).thenReturn(EntityMovementMode.WIGE);
        Mockito.when(mockDropship.isDestroyed()).thenReturn(false);
        Mockito.when(mockDropship.getElevation()).thenReturn(1);
        Vector<Integer> ids2 = player.getAirborneVTOL();
        TestCase.assertEquals(2, ids2.size());
        TestCase.assertTrue(ids2.contains(4));
        TestCase.assertTrue(ids2.contains(3));
    }

    @Test
    public void testPlayerRating() {
        TestCase.assertEquals(0, player.getPlayerRating());
        player.setPlayerRating(100);
        TestCase.assertEquals(100, player.getPlayerRating());
    }
}