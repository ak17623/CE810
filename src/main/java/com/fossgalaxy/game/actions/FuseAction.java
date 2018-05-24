package com.fossgalaxy.game.actions;

import com.fossgalaxy.game.orders.FuseOrder;
import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;
import java.util.Objects;

/**
 * Created by za17984 on 16/05/2018.
 */
public class FuseAction extends AbstractAction {
    private EntityType otherType;
    private EntityType resultType;

    @ObjectDef("Fuse")
    public FuseAction(EntityType other, EntityType result){
        this.otherType = Objects.requireNonNull(other);
        this.resultType = Objects.requireNonNull(result);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        Entity targetPos = s.getEntityAt(co);
        //if (){

        //}
        if (targetPos == null) {
            return false;
        }

        if (!otherType.equals(targetPos.getType())) {
            return false;
        }

        if (targetPos.getOwner() != entity.getOwner()) {
            return false;
        }

        if (s.getDistance(entity.getPos(), co) != 1){
            return false;
        }

        if (targetPos.getHealth() == 0) {
            return false;
        }

        return true;
    }

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        return new FuseOrder(co, resultType);
    }

    @Override
    public int getRange(Entity entity) {
        return 1;
    }

    @Override
    public Color getHintColour() {
        return com.fossgalaxy.games.tbs.actions.AbstractAction.HINT_BUILD_COLOUR;
    }

    @Override
    public Color getBorderColour() {
        return Color.GREEN;
    }

    public String toString(){
        return "Fuse";
    }

    @Override
    public String getCategory() {
        return com.fossgalaxy.games.tbs.actions.AbstractAction.CAT_UPGRADE;
    }
}
