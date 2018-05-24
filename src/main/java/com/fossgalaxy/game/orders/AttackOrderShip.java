package com.fossgalaxy.game.orders;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.io.AudioRegistry;
import com.fossgalaxy.games.tbs.order.AttackOrder;
import com.fossgalaxy.games.tbs.parameters.EntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Ranged attacks don't deal damage to the defender.
 * <p>
 * Created by webpigeon on 16/10/17.
 */
public class AttackOrderShip extends AttackOrder {

    public AttackOrderShip(Entity target) {
        super(target.getID(), "atkRanged", "defRanged");
    }

    public void doOrder(Entity entity, GameState state) {
        Entity target = getTarget(state);
        if (target == null) {
            return;
        }

        List<Entity> targets = new ArrayList<>();

        EntityType type = target.getType();
        if ("Base_11".equals(type.getName()) || "Base_22".equals((type.getName()))) {

            Collection<Entity> myEntities = state.getOwnedEntities(target.getOwner());
            for (Entity possibleTarget : myEntities) {
                EntityType possibleTargetType = possibleTarget.getType();
                if ("Base_11".equals(possibleTargetType.getName())) {
                    targets.add(possibleTarget);
                }
                if ("Base_22".equals(possibleTargetType.getName())) {
                    targets.add(possibleTarget);
                }
            }

        } else {
            targets.add(target);
        }

        int range = state.getDistance(entity.getPos(), target.getPos());
        int maxRange = entity.getType().getProperty("attackRange", 0);
        if (range > maxRange) {
            return;
        }

        //murdering one's self is counterproductive
        if (entity.equals(target)) {
            return;
        }

        for (Entity targetEntity : targets) {

            int damageToTarget = getDamage(entity, targetEntity);
            targetEntity.setHealth(targetEntity.getHealth() - damageToTarget);

            //kill the entity if they run out of health.
            //this needs to be done when the order is processed, else it could effect other entities' moves.
            //eg, A murders B, C moves into B's space.
            if (targetEntity.getHealth() <= 0) {
                state.removeEntity(targetEntity);

                Collection<Entity> enemyEntities = state.getOwnedEntities(targetEntity.getOwner());
                List<Entity> enemyShips = new ArrayList<>();
                List<Entity> enemyBases = new ArrayList<>();

                for (Entity enemyEntity : enemyEntities) {
                    EntityType enemyType = enemyEntity.getType();
                    if ("Ship1".equals(enemyType.getName())) {
                        enemyShips.add(enemyEntity);
                    } else if ("Base_11".equals(enemyType.getName())) {
                        enemyBases.add(enemyEntity);
                    }
                }

                if (enemyShips.isEmpty()) {
                    for (Entity base : enemyBases) {
                        base.setProperty("overdrive", 1);
                    }
                }
            }
        }

        //add XP for killing a unit.
        entity.setProperty("xp", target.getProperty("xpAward", 0));

    }
}


