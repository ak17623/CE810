package com.fossgalaxy.game.ai.rules;


import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.actions.AbstractAction;
import com.fossgalaxy.games.tbs.ai.rules.FirstLegalRule;
import com.fossgalaxy.games.tbs.ai.rules.MovementRule;
import com.fossgalaxy.games.tbs.ai.rules.PerEntityRule;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.Collection;
import java.util.List;

public class MergeToMake extends FirstLegalRule {
    private EntityType mergeWith;

    @ObjectDef("MergeRule")
    public MergeToMake(EntityType merge) {
        super(AbstractAction.CAT_UPGRADE);
        this.mergeWith = merge;
    }

    @Override
    protected CubeCoordinate chooseTarget(Entity entity, GameState state) {

        Collection<CubeCoordinate> neighbors = state.getNeighborPos(entity.getPos());
        for (CubeCoordinate neighbor : neighbors) {
            Entity neighborEntity = state.getEntityAt(neighbor);
            if (neighborEntity == null) {
                continue;
            }

            if (neighborEntity.getOwner() != entity.getOwner()) {
                continue;
            }

            if (neighbor.equals(entity.getPos())) {
                continue;
            }

            if (mergeWith.equals(neighborEntity.getType())) {
                return neighbor;
            }
        }

        return null;
    }
}