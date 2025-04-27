package io.github._20nickaname20.imbored.items.usable.joint;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickaname20.imbored.entities.damagable.living.human.CursorEntity;
import io.github._20nickaname20.imbored.items.usable.JointItem;

public class DistanceJointItem extends JointItem {
    public DistanceJointItem() {
        super(0.5f, 10f);
    }

    @Override
    public void createJoint(Body bodyA, Body bodyB, Vector2 posA, Vector2 posB) {
        DistanceJointDef defJoint = new DistanceJointDef();
        defJoint.collideConnected = true;
        defJoint.frequencyHz = 5;
        defJoint.dampingRatio = 0.5f;
        defJoint.initialize(bodyA, bodyB, posA, posB);
        defJoint.length *= 0.75f;
        DistanceJoint joint = (DistanceJoint) bodyA.getWorld().createJoint(defJoint);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Color.CYAN);
        if (handHolder != null && this.isATargeted()) {
            renderer.circle(0, 0, 1.5f, 6);

            Matrix4 matrix4 = renderer.getTransformMatrix().cpy();
            renderer.identity();

            Vector2 posB = handHolder.getCursorPosition();
            if (this.posA.dst(posB) > this.maxDistance) {
                renderer.setColor(0.8f, 0.3f, 0.3f, 1);
            }

            renderer.line(this.posA, posB);

            renderer.setTransformMatrix(matrix4);
            renderer.updateMatrices();
        } else {
            renderer.line(-1, -1, 1, 1);
        }
    }
}
