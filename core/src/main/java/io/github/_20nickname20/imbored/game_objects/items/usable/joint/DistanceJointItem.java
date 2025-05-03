package io.github._20nickname20.imbored.game_objects.items.usable.joint;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.JointItem;
import io.github._20nickname20.imbored.render.JointDisplay;

public abstract class DistanceJointItem extends JointItem {
    private final float frequency, damping;

    public DistanceJointItem(Entity holder, float size, float maxDistance, Color color, float frequency, float damping) {
        super(holder, size, maxDistance, color);
        this.frequency = frequency;
        this.damping = damping;
    }

    @Override
    public void createJoint(Body bodyA, Body bodyB, Vector2 posA, Vector2 posB) {
        DistanceJointDef defJoint = new DistanceJointDef();
        defJoint.collideConnected = true;
        defJoint.frequencyHz = frequency;
        defJoint.dampingRatio = damping;
        defJoint.initialize(bodyA, bodyB, posA, posB);
        DistanceJoint joint = (DistanceJoint) bodyA.getWorld().createJoint(defJoint);
        joint.setUserData(new JointDisplay(this.color));
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(color);
        if (handHolder != null && this.isATargeted()) {
            renderer.circle(0, 0, 1.75f, 6);

            Matrix4 matrix4 = renderer.getTransformMatrix().cpy();
            renderer.identity();

            Vector2 posB = handHolder.getCursorPosition();
            if (this.posA.dst(posB) > this.maxDistance || getClosestBodyForConnecting(handHolder.world, handHolder.getCursorPosition()) == null) {
                renderer.setColor(0.8f, 0.3f, 0.3f, 1);
            }

            renderer.line(this.posA, posB);

            renderer.setTransformMatrix(matrix4);
            return;
        }
        if (handHolder != null && getClosestBodyForConnecting(handHolder.world, handHolder.getCursorPosition()) != null) {
            renderer.circle(0, 0, 1.75f, 6);
            return;
        }
        renderer.line(-1, -1, 1, 1);
    }
}
