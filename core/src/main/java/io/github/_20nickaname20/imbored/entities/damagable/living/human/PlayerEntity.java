package io.github._20nickaname20.imbored.entities.damagable.living.human;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import io.github._20nickaname20.imbored.*;
import io.github._20nickaname20.imbored.entities.BlockEntity;
import io.github._20nickaname20.imbored.entities.damagable.living.HumanEntity;
import io.github._20nickaname20.imbored.items.UsableItem;
import io.github._20nickaname20.imbored.items.usable.guns.raycast.TestGunItem;

import java.util.ArrayList;
import java.util.List;

public class PlayerEntity extends HumanEntity {
    float food, thirst, infection; //TODO: set values

    PlayerController controller;
    private float lastJumpTime = 0;

    private Mode mode = Mode.GRAB;

    public void setMode(Mode mode) {
        if (mode != Mode.GRAB) {
            put();
        }
        if (mode != Mode.INV) {
            stopUsingItem();
        }
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    private float maxGrabForce = 2000;
    private float maxGrabMass = 10;
    private float grabRadius = 6;
    private float grabCursorDistance = 8;
    private float throwPower = 55;

    private float itemCursorDistance = 4;

    public float getCursorDistance() {
        if (mode == Mode.GRAB) {
            return grabCursorDistance;
        }
        if (mode == Mode.INV) {
            return itemCursorDistance;
        }
        return 1;
    }

    public Vector2 cursorDirection = new Vector2(1, 0);
    public float cursorRotationVel = 0;
    private BlockEntity grabbedEntity;
    private MouseJoint grabJoint;
    // private DistanceJoint grabDistJoint;

    public Inventory inventory = new Inventory(this, 10);

    public PlayerEntity(World world, float x, float y, PlayerController controller) {
        super(world, x, y, 100);
        controller.setPlayer(this);
        this.controller = controller;
        controller.register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new PlayerEntity(world, 0, 0, controller).inventory.add(new TestGunItem());
    }

    public void jump() {
        if (this.getTimeSinceContact() > 0.2f) return;
        if (Util.time() - lastJumpTime < 0.2f) return;
        lastJumpTime = Util.time();
        this.b.applyLinearImpulse(new Vector2(0, 25), this.b.getPosition(), true);
    }

    public void scrollItem(int amount) {
        stopUsingItem();
        inventory.scroll(amount);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        cursorDirection.rotateDeg(cursorRotationVel * dt);
        if (grabbedEntity != null) {
            Vector2 cursorPos = getCursorPosition().cpy();
            if (grabbedEntity.b.getPosition().sub(b.getPosition()).dot(cursorDirection) < -0.5) {
                put();
                return;
            }
            Vector2 impulse = cursorPos.sub(grabbedEntity.b.getPosition());
            float len = impulse.len();
            if (len > grabRadius * 2) {
                put();
                return;
            }
            grabJoint.setTarget(getCursorPosition());
        }
        inventory.update(dt);

        // TODO: REmove this
        if (b.getPosition().y > 50) {
            b.applyLinearImpulse(0, -100, b.getPosition().x, b.getPosition().y, true);
        }
    }

    public Vector2 getCursorRelative() {
        return cursorDirection.cpy().scl(getCursorDistance());
    }

    public Vector2 getCursorPosition() {
        return this.b.getPosition().cpy().add(getCursorRelative());
    }

    public void grab() {
        if (grabbedEntity != null) return;
        Vector2 cursorPosition = getCursorPosition();
        Array<Body> bodies = new Array<>();
        this.world.getBodies(bodies);
        List<Body> nearBodies = new ArrayList<>(10);
        for (Body body : bodies) {
            Entity entity = Entity.getEntity(body);
            if (entity == null) continue;
            if (!(entity instanceof BlockEntity)) continue;
            if (body.getMass() > maxGrabMass) continue;
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
        boolean found = false;
        Vector2 grabPoint = new Vector2();
        for (Fixture fixture : closest.getFixtureList()) {
            if (fixture.testPoint(cursorPosition)) {
                grabPoint.set(cursorPosition);
                found = true;
                break;
            }
        }
        if (!found) {
            closest.getWorld().rayCast((Fixture fixture, Vector2 point, Vector2 normal, float fraction) -> {
                grabPoint.set(point);
                return 0;
            }, cursorPosition, closest.getPosition());
        }

        mouseJointDef.target.set(grabPoint);
        mouseJointDef.bodyA = this.b;
        mouseJointDef.bodyB = closest;
        mouseJointDef.maxForce = maxGrabForce;

        mouseJointDef.collideConnected = Entity.getVolume(closest) > 12;
        //mouseJointDef.frequencyHz = 10000f
        grabJoint = (MouseJoint) this.world.createJoint(mouseJointDef);
        grabJoint.setTarget(getCursorPosition());

        //DistanceJointDef defJoint = new DistanceJointDef ();
        //defJoint.frequencyHz = 5;
        //defJoint.dampingRatio = 0.5f;
        //defJoint.initialize(this.b, closest, this.b.getPosition(), grabPoint);
        //DistanceJoint distanceJoint = (DistanceJoint) this.world.createJoint(defJoint);
        //grabDistJoint = distanceJoint;
        // TODO: make some joints hidden (make custom joint class)
        //  joint.setUserData();

        grabbedEntity = (BlockEntity) closest.getUserData();
        grabbedEntity.grabber = this;
        System.out.println("grabbed entity: " + grabbedEntity);
    }

    public void put() {
        if (grabJoint == null) return;
        grabbedEntity.grabber = null;
        grabbedEntity = null;
        this.world.destroyJoint(grabJoint);
        grabJoint = null;
        // this.world.destroyJoint(grabDistJoint);
        // grabDistJoint = null;
    }

    public void throwGrabbed() {
        if (grabJoint == null) return;
        grabbedEntity.b.applyLinearImpulse(cursorDirection.cpy().scl(throwPower), grabJoint.getAnchorB(), true);
        b.applyLinearImpulse(cursorDirection.cpy().scl(-throwPower / 4), this.b.getPosition(), true);
        put();
    }

    public void startUsingItem() {
        Item item = inventory.getSelectedItem();
        if (item == null) return;
        if (!(item instanceof UsableItem usable)) return;
        usable.onStartUse(this);
    }

    public void stopUsingItem() {
        Item item = inventory.getSelectedItem();
        if (item == null) return;
        if (!(item instanceof UsableItem usable)) return;
        usable.onEndUse(this);
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

    void renderSlot(ShapeRenderer renderer, boolean active, float x, float y) {
        renderer.rect(x - 2, y - 2, 4, 4);
        if (active) {
            renderer.rect(x - 2.2f, y - 2.2f, 4.4f, 4.4f);
        }
    }

    void renderInv(ShapeRenderer renderer) {
        renderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        for (int i = -1; i < 2; i++) {
            renderSlot(renderer, i == 0, i * 4.6f, -8);
            Item item = inventory.get(i + inventory.getSelectedSlot());
            if (item != null) {
                renderer.translate(i * 4.6f, -8, 0);
                // renderer.rotate(0, 0, 1, MathUtils.radiansToDegrees * 3.14f);

                item.render(renderer, false);

                renderer.translate(-i * 4.6f, 8, 0);
            }
        }
    }

    @Override
    public boolean render(ShapeRenderer renderer) {
        super.render(renderer);
        float cursorDistance = getCursorDistance();
        renderer.setColor(1, 1,1, 0.5f);

        float angle = cursorDirection.angleDeg();
        renderer.rotate(0, 0, 1, angle);
        renderer.line(0, 0, cursorDistance, 0);
        renderer.translate(cursorDistance, 0, 0);
            renderer.circle(0, 0,1.2f);
            Item selectedItem = inventory.getSelectedItem();
            if (mode == Mode.INV && selectedItem != null) {
                selectedItem.render(renderer, true);
            }
        renderer.translate(-cursorDistance, 0, 0);
        renderer.rotate(0, 0, 1, -angle);

        if (mode == Mode.INV) {
            renderInv(renderer);
        }
        return false;
    }

    public enum Mode {
        GRAB,
        INV
    }
}
