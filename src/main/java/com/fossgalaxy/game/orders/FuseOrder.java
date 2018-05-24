package com.fossgalaxy.game.orders;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import org.codetome.hexameter.core.api.CubeCoordinate;

/**
 * Created by za17984 on 16/05/2018.
 */
public class FuseOrder implements Order {
    private CubeCoordinate pos;
    private EntityType result;

    public FuseOrder(CubeCoordinate pos, EntityType result){
        this.pos = pos;
        this.result = result;
    }

    @Override
    public void doOrder(Entity host, GameState state) {

        Entity otherPos = state.getEntityAt(pos);
        if (otherPos == null) {
            return;
        }

        if (otherPos.getOwner() != host.getOwner()) {
            return;
        }

        if (state.getDistance(host.getPos(), pos) != 1){
            return;
        }

        //state.removeEntity(host);
        //state.removeEntity(otherPos);

        host.setHealth(0);
        otherPos.setHealth(0);

        Entity e = new Entity(result, host.getPos(), host.getOwner());
        state.addEntity(e);
    }

}
