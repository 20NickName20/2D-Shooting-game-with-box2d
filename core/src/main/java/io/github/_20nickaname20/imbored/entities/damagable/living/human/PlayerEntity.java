package io.github._20nickaname20.imbored.entities.damagable.living.human;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import io.github._20nickaname20.imbored.*;
import io.github._20nickaname20.imbored.entities.BlockEntity;
import io.github._20nickaname20.imbored.entities.InventoryHolder;
import io.github._20nickaname20.imbored.entities.damagable.living.HumanEntity;
import io.github._20nickaname20.imbored.items.UsableItem;
import io.github._20nickaname20.imbored.items.usable.guns.raycast.TestGunItem;
import io.github._20nickaname20.imbored.items.usable.guns.raycast.automatic.AutomaticRifleItem;
import io.github._20nickaname20.imbored.items.usable.joint.DistanceJointItem;

import java.util.ArrayList;
import java.util.List;

public class PlayerEntity extends HumanEntity implements InventoryHolder {
    float food, thirst, infection; //TODO: set values

    PlayerController controller;
    private float lastJumpTime = 0;

    private Mode mode = Mode.GRAB;
    private float maxGrabForce = 2000;
    private float maxGrabMass = 10;
    private float grabRadius = 6;
    private float grabCursorDistance = 8;
    private float throwPower = 55;

    private float itemCursorDistance = 4;

    private float cursorDistance = getModeCursorDistance();


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

        while (inventory.getFreeSpace() > 2) {
            if (MathUtils.randomBoolean()) {
                inventory.add(new TestGunItem());
                continue;
            }
            inventory.add(new AutomaticRifleItem());
        }
        while (inventory.getFreeSpace() > 0) {
            inventory.add(new DistanceJointItem());
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        this.cursorDistance = getModeCursorDistance();
        if (mode != Mode.GRAB) {
            put();
        }
        if (mode != Mode.INV) {
            deselectItem();
        } else {
            selectItem();
        }
    }

    public float getModeCursorDistance() {
        if (mode == Mode.GRAB) {
            return grabCursorDistance;
        }
        if (mode == Mode.INV) {
            return itemCursorDistance;
        }
        return 1;
    }

    public float getCursorDistance() {
        return cursorDistance;
    }

    public void setCursorDistance(float distance) {
        this.cursorDistance = distance;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        new PlayerEntity(world, 0, 0, controller);
    }

    public void jump() {
        if (this.getTimeSinceContact() > 0.2f) return;
        if (Util.time() - lastJumpTime < 0.2f) return;
        lastJumpTime = Util.time();
        this.b.applyLinearImpulse(new Vector2(0, 25), this.b.getPosition(), true);
    }

    public void scrollItem(int amount) {
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

        Body closest = Util.getClosestBodyFiltered(world, cursorPosition, (body) -> {
            Entity entity = Entity.getEntity(body);
            if (entity == null) return false;
            if (!(entity instanceof BlockEntity)) return false;
            if (body.getMass() > maxGrabMass) return false;
            if (body.getPosition().dst(cursorPosition) > grabRadius) return false;
            return true;
        });
        if (closest == null) return;

        Vector2 grabPoint = Util.getBodyClosestPoint(closest, cursorPosition);

        MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.target.set(grabPoint);
        mouseJointDef.bodyA = this.b;
        mouseJointDef.bodyB = closest;
        mouseJointDef.maxForce = maxGrabForce;
        mouseJointDef.collideConnected = Entity.getVolume(closest) > 16;
        grabJoint = (MouseJoint) this.world.createJoint(mouseJointDef);
        grabJoint.setTarget(getCursorPosition());

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
        if (mode != Mode.INV) return;
        Item item = inventory.getSelectedItem();
        if (item == null) return;
        if (!(item instanceof UsableItem usable)) return;
        usable.onStartUse(this);
    }

    public void stopUsingItem() {
        if (mode != Mode.INV) return;
        Item item = inventory.getSelectedItem();
        if (item == null) return;
        if (!(item instanceof UsableItem usable)) return;
        usable.onEndUse(this);
    }

    public void selectItem() {
        Item item = inventory.getSelectedItem();
        if (item == null) return;
        item.onSelect(this);
    }

    public void deselectItem() {
        Item item = inventory.getSelectedItem();
        if (item == null) return;
        item.onDeselect(this);
    }

    public void removeSelectedItem() {
        inventory.removeSelectedItem();
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
            renderer.translate(0, -7, 0);
            inventory.renderPart(renderer, 3);
            renderer.translate(0, 7, 0);
        }
        return false;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public enum Mode {
        GRAB,
        INV
    }
}
