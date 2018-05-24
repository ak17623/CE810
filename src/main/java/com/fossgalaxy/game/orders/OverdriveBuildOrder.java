package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.actions.BuildAction;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.Map;

public class OverdriveBuildOrder extends BuildOrder {

    private ResourceType lockResource;

    public OverdriveBuildOrder(EntityType type, CubeCoordinate cube, ResourceType lockResource) {
        super(type, cube);
        this.lockResource = lockResource;
    }

    @Override
    public void doOrder(Entity host, GameState state) {
        if (host.getProperty("overdrive") > 0) {
            doBuild(host, state);
            host.setProperty("overdrive", 0);
            return;
        }

        int lockResourceValue = state.getResource(host.getOwner(), lockResource);
        if (lockResourceValue > state.getTime()) {
            return;
        }

        doBuild(host, state);
        state.setResource(host.getOwner(), lockResource, state.getTime()+1);
    }

    public void doBuild(Entity host, GameState state) {
        if (isSpaceEmpty(state)) return;
        if (!payIfPossible(host, state)) return;
        build(host, state);
    }

}
