package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;

public abstract class JointItem extends UsableItem {
    private Body bodyA, bodyB;
    private Vector2 bodyAPos;
    private float bodyAAngle;
    protected Vector2 posA, posB;
    private boolean isATargeted = false;
    protected final float maxDistance;
    private float reach = 6;

    protected final Color color;

    public JointItem(Entity holder, float size, float maxDistance, Color color) {
        super(holder, size);
        this.maxDistance = maxDistance;
        this.color = color;
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
        Body closest = getClosestBodyForConnecting(player.world, pos);
        if (closest == null) return;
        if (closest.getUserData() instanceof BlockEntity) {
            isATargeted = true;
            bodyA = closest;
            bodyAPos = bodyA.getPosition().cpy();
            bodyAAngle = bodyA.getAngle();
            posA = FindBody.closestPoint(closest, pos);
        }
    }

    protected Body getClosestBodyForConnecting(World world, Vector2 pos) {
        return FindBody.closestFiltered(world, pos, (body) -> {
            Entity entity = Entity.getEntity(body);
            if (entity == null) return false;
            if (!(entity instanceof BlockEntity)) return false;
            if (body.getPosition().dst(pos) > reach) return false;
            return true;
        });
    }

    @Override
    public void onEndUse(PlayerEntity player) {
        isATargeted = false;
        if (bodyA == null) return;
        Vector2 pos = player.getCursorPosition();
        Body closest = getClosestBodyForConnecting(player.world, pos);
        if (closest == null) return;
        Vector2 connectPoint = FindBody.closestPoint(closest, pos);
        if (closest == bodyA) return;
        if (bodyA.getPosition().dst(bodyAPos) > 0.1) return;
        if (!MathUtils.isEqual(bodyA.getAngle(), bodyAAngle, 0.01f)) return;
        if (connectPoint.dst(posA) > maxDistance) return;
        if (closest.getUserData() instanceof BlockEntity) {
            bodyB = closest;
            posB = connectPoint;
            createJoint(bodyA, bodyB, posA, posB);
            player.removeEquippedItem();
        }
    }

    @Override
    public float getCursorDistance() {
        return 7;
    }

    @Override
    public void onUnequip(PlayerEntity holder) {
        super.onUnequip(holder);
        reset();
    }

    public abstract void createJoint(Body bodyA, Body bodyB, Vector2 posA, Vector2 posB);
}
