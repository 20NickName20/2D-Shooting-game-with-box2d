package io.github._20nickaname20.imbored.entities.living.human;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import io.github._20nickaname20.imbored.Entity;
import io.github._20nickaname20.imbored.PlayerController;
import io.github._20nickaname20.imbored.Util;
import io.github._20nickaname20.imbored.entities.BlockEntity;
import io.github._20nickaname20.imbored.entities.living.HumanEntity;

import java.util.ArrayList;
import java.util.List;

public class PlayerEntity extends HumanEntity {
    float food, thirst, lifeEnergy, infection; //TODO: set values

    PlayerController controller;
    private float lastJumpTime = 0;

    private float maxGrabForce = 200;
    private float grabRadius = 6;
    private float cursorDistance = 8;
    private float throwPower = 40;

    public Vector2 cursorDirection = new Vector2(1, 0);
    public float cursorRotationVel = 0;
    private BlockEntity grabbedEntity;
    private MouseJoint grabJoint;

    public PlayerEntity(World world, float x, float y, PlayerController controller) {
        super(world, x, y);
        controller.setPlayer(this);
        this.controller = controller;
        controller.register();
    }

    public void jump() {
        if (this.getTimeSinceContact() > 0.2f) return;
        if (Util.time() - lastJumpTime < 0.2f) return;
        lastJumpTime = Util.time();
        this.b.applyLinearImpulse(new Vector2(0, 25), this.b.getPosition(), true);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        cursorDirection.rotateDeg(cursorRotationVel * dt);
        if (grabbedEntity != null) {
            Vector2 cursorPos = getCursorPosition().cpy();
            Vector2 impulse = cursorPos.sub(grabbedEntity.b.getPosition());
            float len = impulse.len();
            if (len > grabRadius * 3) {
                put();
                return;
            }
            grabJoint.setTarget(getCursorPosition());
        }
    }

    public Vector2 getCursorPosition() {
        return this.b.getPosition().cpy().add(cursorDirection.cpy().nor().scl(cursorDistance));
    }

    public void grab() {
        if (grabbedEntity != null) return;
        Vector2 cursorPosition = getCursorPosition();
        Array<Body> bodies = new Array<>();
        this.b.getWorld().getBodies(bodies);
        List<Body> nearBodies = new ArrayList<>(10);
        for (Body body : bodies) {
            Entity entity = Entity.getEntity(body);
            if (entity == null) continue;
            if (!(entity instanceof BlockEntity)) continue;
            if (body.getPosition().dst(cursorPosition) > grabRadius) continue;
            nearBodies.add(body);
        }
        if (nearBodies.isEmpty()) return;

        Body closest = nearBodies.remove(0);
        float closestDist = closest.getPosition().dst(cursorPosition);
        if (!nearBodies.isEmpty()) {
            for (Body body : nearBodies) {
                float dist = body.getPosition().dst(cursorPosition);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = body;
                }
            }
        }

        MouseJointDef mouseJointDef = new MouseJointDef();
        if (closest.getFixtureList().get(0).testPoint(getCursorPosition())) {
            mouseJointDef.target.set(getCursorPosition());
        } else {
            mouseJointDef.target.set(closest.getPosition());
        }
        mouseJointDef.bodyA = this.b;
        mouseJointDef.bodyB = closest;
        mouseJointDef.maxForce = maxGrabForce;
        mouseJointDef.collideConnected = true;
        mouseJointDef.frequencyHz = 20f;
        grabJoint = (MouseJoint) this.b.getWorld().createJoint(mouseJointDef);
        grabJoint.setTarget(getCursorPosition());;
        grabbedEntity = (BlockEntity) closest.getUserData();
        grabbedEntity.isGrabbed = true;
        System.out.println("grabbed entity: " + grabbedEntity);
    }

    public void put() {
        if (grabJoint == null) return;
        grabbedEntity.isGrabbed = false;
        grabbedEntity = null;
        b.getWorld().destroyJoint(grabJoint);
        grabJoint = null;
    }

    public void throwGrabbed() {
        if (grabJoint == null) return;
        grabbedEntity.b.applyLinearImpulse(cursorDirection.cpy().nor().scl(throwPower), grabJoint.getAnchorB(), true);
        b.applyLinearImpulse(cursorDirection.cpy().nor().scl(-throwPower / 4), this.b.getPosition(), true);
        put();
    }

    @Override
    public void beginContact(Body other) {
        if (other.getUserData() instanceof PlayerEntity) return;
        super.beginContact(other);
    }

    @Override
    public void endContact(Body other) {
        if (other.getUserData() instanceof PlayerEntity) return;
        super.endContact(other);
    }

    @Override
    public void remove() {
        put();
        super.remove();
        controller.unregister();
    }

    @Override
    public boolean render(ShapeRenderer renderer) {
        renderer.setColor(1, 1,1, 0.5f);
        renderer.line(b.getPosition(), getCursorPosition());
        return false;
    }
}
