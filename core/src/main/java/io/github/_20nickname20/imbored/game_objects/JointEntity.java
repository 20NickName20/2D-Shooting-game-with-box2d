package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.util.FindBody;

public class JointEntity {
    public final GameWorld world;
    public final Color color;
    public final Vector2 initPoint1, initPoint2;
    public Joint j;
    public final float frequency, damping;

    public JointEntity(GameWorld world, Color color, Vector2 point1, Vector2 point2, float frequency, float damping) {
        this.world = world;
        this.color = color;
        this.initPoint1 = point1;
        this.initPoint2 = point2;
        this.frequency = frequency;
        this.damping = damping;
    }

    public JointEntity(GameWorld world, JointData data) {
        this.world = world;
        this.color = new Color(data.r, data.g, data.b, 1);
        this.initPoint1 = new Vector2(data.x1, data.y1);
        this.initPoint2 = new Vector2(data.x2, data.y2);
        this.frequency = data.frequency;
        this.damping = data.damping;
    }

    public void spawn() {
        world.spawn(this);
    }

    public void remove() {
        world.remove(j);
    }

    public void onSpawn(World world) {
        DistanceJointDef defJoint = new DistanceJointDef();
        defJoint.collideConnected = true;
        defJoint.frequencyHz = frequency;
        defJoint.dampingRatio = damping;
        defJoint.initialize(FindBody.atPoint(world, initPoint1), FindBody.atPoint(world, initPoint2), initPoint1, initPoint2);
        DistanceJoint joint = (DistanceJoint) world.createJoint(defJoint);
        joint.setUserData(this);
        this.j = joint;
    }

    public JointData createPersistentData() {
        JointData data = new JointData();
        data.r = color.r;
        data.g = color.g;
        data.b = color.b;
        data.frequency = this.frequency;
        data.damping = this.damping;
        Vector2 point1 = j.getAnchorA();
        data.x1 = point1.x;
        data.y1 = point1.y;
        Vector2 point2 = j.getAnchorB();
        data.x2 = point2.x;
        data.y2 = point2.y;

        return data;
    }

    public static class JointData {
        float r, g, b;
        float x1, y1, x2, y2;
        public float frequency, damping;
    }
}
