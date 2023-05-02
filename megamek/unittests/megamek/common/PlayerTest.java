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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.io.File;
import java.util.Vector;

@RunWith(JUnit4.class)
public class PlayerTest {
    private Player player;
    private Game game;
    private Entity mockedTank;
    private GameOptions mockedOptions;
    private Crew mockedCrew;
    private VTOL mockedVTOL;


    @Before
    public void setup(){
        game = Mockito.mock(Game.class);
        player = new Player(1, "test_player");

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
}