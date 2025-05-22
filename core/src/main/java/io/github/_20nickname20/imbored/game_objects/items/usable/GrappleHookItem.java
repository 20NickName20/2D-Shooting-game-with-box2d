package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Raycast;

public class GrappleHookItem extends UsableItem {

    private final static float RANGE = 90f;

    public GrappleHookItem() {
        super();
    }

    public GrappleHookItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return 3;
    }

    private DistanceJoint joint = null;
    private Entity grabbedEntity = null;

    public void shootRay(PlayerEntity player) {
        Body playerBody = player.b;
        Vector2 endPosition = playerBody.getPosition().cpy().add(player.getCursorDirection().scl(RANGE));
        Raycast.Result result = Raycast.castClosest(player.world, playerBody, playerBody.getPosition(), endPosition);
        if (result == null) return;
        if (!(result.body.getUserData() instanceof Entity entity)) return;
        this.grabbedEntity = entity;
        createJoint(player, entity, result.point);
    }

    public void createJoint(PlayerEntity player, Entity entity, Vector2 point) {
        DistanceJointDef defJoint = new DistanceJointDef();
        defJoint.collideConnected = true;
        defJoint.frequencyHz = 0.7f;
        defJoint.dampingRatio = 0.7f;
        defJoint.initialize(player.b, entity.b, player.b.getPosition(), point);
        joint = (DistanceJoint) player.world.createJoint(defJoint);
        joint.setUserData(Color.WHITE);
    }


    @Override
    public void onStartUse(PlayerEntity player) {
        if (joint != null) {
            player.world.destroyJoint(joint);
            joint = null;
        }
        shootRay(player);
    }

    @Override
    public void update(float dt) {
        if (joint != null) {
            if (grabbedEntity.isRemoved()) {
                joint = null;
                return;
            }
            float length = joint.getLength();
            float realLength = joint.getAnchorA().dst(joint.getAnchorB());
            if (length > realLength) {
                length = realLength - dt * 200f;
            }
            if (length > 3) {
                joint.setLength(length - dt * 25);
            }
        }
    }

    @Override
    public void onEndUse(PlayerEntity player) {
        if (grabbedEntity == null) return;
        if (grabbedEntity.isRemoved()) return;
        if (joint == null) return;
        player.world.destroyJoint(joint);
        joint = null;
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        renderer.withRotation(40f, () -> {
            renderer.rect(-0.9f, -0.4f, 1f, 0.8f);
        });
        renderer.polygon(new float[]{
            0f, 0.5f,
            2f, 0.9f,
            2f, -0.9f,
            0f, -0.5f,
        });
        renderer.setColor(Color.WHITE);
        renderer.rect(-0.1f, -0.1f, 3, 0.2f);
    }
}
