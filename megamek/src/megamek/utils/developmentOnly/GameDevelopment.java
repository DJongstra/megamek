package megamek.utils.developmentOnly;

import megamek.common.Coords;
import megamek.common.Entity;
import megamek.common.IGame;

import java.util.*;

public class GameDevelopment {
    private static int countEntitiesInCache(List<Integer> entitiesInCache, Map<Coords, HashSet<Integer>> entityPosLookup) {
        int count = 0;
        for (Coords c : entityPosLookup.keySet()) {
            count += entityPosLookup.get(c).size();
            entitiesInCache.addAll(entityPosLookup.get(c));
        }
        return count;
    }

    /**
     * A check to ensure that the position cache is properly updated.  This
     * is only used for debugging purposes, and will cause a number of things
     * to slow down.
     */
    @SuppressWarnings("unused")
    public static void checkPositionCacheConsistency(IGame game, Map<Coords, HashSet<Integer>> entityPosLookup) {
        // Sanity check on the position cache
        //  This could be removed once we are confident the cache is working
        List<Integer> entitiesInCache = new ArrayList<Integer>();
        List<Integer> entitiesInVector = new ArrayList<Integer>();
        int entitiesInCacheCount = countEntitiesInCache(entitiesInCache, entityPosLookup);
        int entityVectorSize = 0;
        for (Entity e : game.getEntitiesVector()) {
            if (e.getPosition() != null) {
                entityVectorSize++;
                entitiesInVector.add(e.getId());
            }
        }
        Collections.sort(entitiesInCache);
        Collections.sort(entitiesInVector);
        IGame.Phase phase = game.getPhase();
        if ((entitiesInCacheCount != entityVectorSize)
                && (phase != IGame.Phase.PHASE_DEPLOYMENT)
                && (phase != IGame.Phase.PHASE_EXCHANGE)
                && (phase != IGame.Phase.PHASE_LOUNGE)
                && (phase != IGame.Phase.PHASE_INITIATIVE_REPORT)
                && (phase != IGame.Phase.PHASE_INITIATIVE)) {
            System.out.println("Entities vector has " + game.getEntitiesVector().size()
                    + " but pos lookup cache has " + entitiesInCache.size()
                    + " entities!");
            List<Integer> missingIds = new ArrayList<Integer>();
            for (Integer id : entitiesInVector) {
                if (!entitiesInCache.contains(id)) {
                    missingIds.add(id);
                }
            }
            System.out.println("Missing ids: " + missingIds);
        }
        for (Entity e : game.getEntitiesVector()) {
            HashSet<Coords> positions = e.getOccupiedCoords();
            for (Coords c : positions) {
                HashSet<Integer> ents = entityPosLookup.get(c);
                if ((ents != null) && !ents.contains(e.getId())) {
                    System.out.println("Entity " + e.getId() + " is in "
                            + e.getPosition() + " however the position cache "
                            + "does not have it in that position!");
                }
            }
        }
        for (Coords c : entityPosLookup.keySet()) {
            for (Integer eId : entityPosLookup.get(c)) {
                Entity e = game.getEntity(eId);
                if (e == null) {
                    continue;
                }
                HashSet<Coords> positions = e.getOccupiedCoords();
                if (!positions.contains(c)) {
                    System.out.println("Entity Position Cache thinks Entity "
                            + eId + "is in " + c
                            + " but the Entity thinks it's in "
                            + e.getPosition());
                }
            }
        }
    }
}
