package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.render.JointDisplay;
import io.github._20nickname20.imbored.util.ClosestRaycast;

public class GrappleHookItem extends UsableItem {

    private final float range = 90f;

    public GrappleHookItem(Entity holder) {
        super(holder, 3);
    }

    private DistanceJoint joint = null;
    private Entity grabbedEntity = null;

    public void shootRay(PlayerEntity player) {
        Body playerBody = player.b;
        Vector2 endPosition = playerBody.getPosition().cpy().add(player.getCursorDirection().scl(range));
        ClosestRaycast.RaycastResult result = ClosestRaycast.cast(player.world, playerBody, playerBody.getPosition(), endPosition);
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
        defJoint.length = 4;
        joint = (DistanceJoint) player.world.createJoint(defJoint);
        joint.setUserData(new JointDisplay(Color.WHITE));
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
    public void onEndUse(PlayerEntity player) {
        if (grabbedEntity == null) return;
        if (grabbedEntity.isRemoved()) return;
        if (joint == null) return;
        player.world.destroyJoint(joint);
        joint = null;
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        renderer.rectLine(-0.9f, -0.9f, 0.1f, 0.1f, 1.1f);
        renderer.polygon(new float[]{
            0f, 0.5f,
            2f, 0.9f,
            2f, -0.9f,
            0f, -0.5f,
        });
        renderer.setColor(Color.WHITE);
        renderer.rectLine(0, 0, 3, 0, 0.2f);
    }
}
