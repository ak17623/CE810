package com.fossgalaxy.game.actions;

import com.fossgalaxy.game.orders.AttackOrderShip;
import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;

import com.fossgalaxy.games.tbs.order.Order;
import org.codetome.hexameter.core.api.CubeCoordinate;

/**
 * Created by za17984 on 16/05/2018.
 */
public class ShipAttack extends RangeAttackAction {

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        Entity e = s.getEntityAt(co);
        if (e == null) {
            return null;
        }

        return new AttackOrderShip(e);
    }
}
