package com.fossgalaxy.game.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.actions.*;
import com.fossgalaxy.games.tbs.actions.AbstractAction;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.order.OverdriveBuildOrder;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.Objects;

/**
 * Created by za17984 on 16/05/2018.
 */
public class OverdriveBuildAction extends BuildAction {

    private ResourceType lockResource;

    @ObjectDef("OverdriveBuild")
    public OverdriveBuildAction(EntityType type, ResourceType lockResource) {
        super(Objects.requireNonNull(type));
        this.lockResource = lockResource;
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        if (!super.isPossible(entity, s, co)) {
            return false;
        }

        boolean isOverdriven = entity.getProperty("overdrive", 0) > 0;
        if (isOverdriven) {
            return true;
        }

        int lockResourceValue = s.getResource(entity.getOwner(), lockResource);
        if (lockResourceValue <= s.getTime()) {
            return true;
        }

        return false;
    }

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        return new OverdriveBuildOrder(type, co, lockResource);
    }

    @Override
    public String getCategory() {
        return AbstractAction.CAT_BUILD;
    }
}
