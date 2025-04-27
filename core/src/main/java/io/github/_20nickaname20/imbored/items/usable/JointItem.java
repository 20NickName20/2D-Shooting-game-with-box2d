package io.github._20nickaname20.imbored.items.usable;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickaname20.imbored.Entity;
import io.github._20nickaname20.imbored.Util;
import io.github._20nickaname20.imbored.entities.BlockEntity;
import io.github._20nickaname20.imbored.entities.InventoryHolder;
import io.github._20nickaname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;
import io.github._20nickaname20.imbored.items.UsableItem;

public abstract class JointItem extends UsableItem {
    private Body bodyA, bodyB;
    private Vector2 bodyAPos;
    private float bodyAAngle;
    protected Vector2 posA, posB;
    private boolean isATargeted = false;
    protected final float maxDistance;
    private float reach = 6;

    public JointItem(float size, float maxDistance) {
        super(size);
        this.maxDistance = maxDistance;
    }

    private void reset() {
        isATargeted = false;
        bodyA = null;
        bodyB = null;
    }

    public boolean isATargeted() {
        return isATargeted;
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        reset();
        Vector2 pos = player.getCursorPosition();
        Body closest = Util.getClosestBodyFiltered(player.world, pos, (body) -> {
            Entity entity = Entity.getEntity(body);
            if (entity == null) return false;
            if (!(entity instanceof BlockEntity)) return false;
            if (body.getPosition().dst(pos) > reach) return false;
            return true;
        });
        if (closest == null) return;
        if (closest.getUserData() instanceof BlockEntity) {
            isATargeted = true;
            bodyA = closest;
            bodyAPos = bodyA.getPosition().cpy();
            bodyAAngle = bodyA.getAngle();
            posA = Util.getBodyClosestPoint(closest, pos);
        }
    }

    @Override
    public void onEndUse(PlayerEntity player) {
        isATargeted = false;
        if (bodyA == null) return;
        Vector2 pos = player.getCursorPosition();
        Body closest = Util.getClosestBodyFiltered(player.world, pos, (body) -> {
            Entity entity = Entity.getEntity(body);
            if (entity == null) return false;
            if (!(entity instanceof BlockEntity)) return false;
            if (body.getPosition().dst(pos) > reach) return false;
            return true;
        });
        if (closest == null) return;
        Vector2 connectPoint = Util.getBodyClosestPoint(closest, pos);
        if (closest == bodyA) return;
        if (bodyA.getPosition().dst(bodyAPos) > 0.1) return;
        if (!MathUtils.isEqual(bodyA.getAngle(), bodyAAngle, 0.01f)) return;
        if (connectPoint.dst(posA) > maxDistance) return;
        if (closest.getUserData() instanceof BlockEntity) {
            bodyB = closest;
            posB = connectPoint;
            createJoint(bodyA, bodyB, posA, posB);
            player.removeSelectedItem();
        }
    }

    @Override
    public void onSelect(InventoryHolder holder) {
        if (holder instanceof PlayerEntity player) {
            player.setCursorDistance(7);
        }
    }

    @Override
    public void onDeselect(InventoryHolder holder) {
        reset();
        if (holder instanceof PlayerEntity player) {
            player.setCursorDistance(player.getDefaultCursorDistance());
        }
    }

    public abstract void createJoint(Body bodyA, Body bodyB, Vector2 posA, Vector2 posB);
}
