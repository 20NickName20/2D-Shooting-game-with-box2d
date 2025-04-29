package io.github._20nickname20.imbored.entities.damagable.living.human.cursor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.*;
import io.github._20nickname20.imbored.*;
import io.github._20nickname20.imbored.entities.BlockEntity;
import io.github._20nickname20.imbored.entities.Grabbable;
import io.github._20nickname20.imbored.entities.InventoryHolder;
import io.github._20nickname20.imbored.entities.damagable.living.human.CursorEntity;
import io.github._20nickname20.imbored.items.UsableItem;
import io.github._20nickname20.imbored.items.usable.guns.raycast.ShotgunItem;
import io.github._20nickname20.imbored.items.usable.guns.raycast.TestGunItem;
import io.github._20nickname20.imbored.items.usable.guns.raycast.automatic.AutomaticRifleItem;
import io.github._20nickname20.imbored.items.usable.joint.distance.HardDistanceJointItem;
import io.github._20nickname20.imbored.render.JointDisplay;
import io.github._20nickname20.imbored.screens.GameScreen;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.util.Util;

import static io.github._20nickname20.imbored.util.With.rotation;
import static io.github._20nickname20.imbored.util.With.translation;

public class PlayerEntity extends CursorEntity implements InventoryHolder {
    float food, thirst, infection; //TODO: set values

    PlayerController controller;
    private float lastJumpTime = 0;

    private Mode mode = Mode.GRAB;
    private float maxGrabForce = 200;
    private float grabRadius = 6;
    private float grabCursorDistance = 8;
    private float throwPower = 55;

    private float itemCursorDistance = 4;

    private BlockEntity grabbedEntity;
    private MouseJoint grabMouseJoint;
    private DistanceJoint grabDistanceJoint;

    public final Inventory inventory = new Inventory(this, 20);

    public PlayerEntity(World world, float x, float y, PlayerController controller) {
        super(world, x, y, 100);
        this.setCursorDistance(getDefaultCursorDistance());
        this.controller = controller;
        controller.register(this);

        while (inventory.getFreeSpace() > 5) {
            inventory.add(switch (MathUtils.random(2)) {
                case 0 -> new TestGunItem(this);
                case 1 -> new AutomaticRifleItem(this);
                default -> new ShotgunItem(this);
            });
        }
        while (inventory.getFreeSpace() > 0) {
            inventory.add(new HardDistanceJointItem(this));
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        this.cursorDistance = getDefaultCursorDistance();
        if (mode != Mode.GRAB) {
            put();
        }
        if (mode != Mode.INV) {
            deselectItem();
        } else {
            selectItem();
        }
    }

    public void nextMode() {
        Mode[] modes = Mode.values();
        for (int i = 1; i <= modes.length; i++) {
            if (modes[i - 1] == mode) {
                setMode(modes[i % modes.length]);
                return;
            }
        }
    }

    @Override
    public float getDefaultCursorDistance() {
        if (mode == Mode.GRAB) {
            return grabCursorDistance;
        }
        if (mode == Mode.INV) {
            return itemCursorDistance;
        }
        return 1;
    }

    public Mode getMode() {
        return mode;
    }

    @Override
    public void onDestroy() {
        if (this.isRemoved()) return;
        GameScreen.spawnEntity(new PlayerEntity(world, 0, 0, controller));
        super.onDestroy();
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
        inventory.update(dt);
        controller.update(dt);

        // TODO: Remove this
        if (b.getPosition().y > 50) {
            b.applyLinearImpulse(0, -100, b.getPosition().x, b.getPosition().y, true);
        }

        if (grabbedEntity != null) {
            Vector2 cursorPosition = getCursorPosition().cpy();
            Vector2 toGrabbed = grabbedEntity.b.getPosition().sub(this.b.getPosition());

            if (toGrabbed.dot(cursorDirection) < -0.5) {
                put();
                return;
            }
            Vector2 impulse = cursorPosition.cpy().sub(grabbedEntity.b.getPosition());
            float len = impulse.len();
            if (len > grabRadius * 2) {
                put();
                return;
            }

            grabMouseJoint.setTarget(cursorPosition);
        }
    }

    public void grab() {
        if (grabbedEntity != null) return;
        Vector2 cursorPosition = this.getCursorPosition();

        Body grabbed = FindBody.closestFiltered(world, cursorPosition, (body) -> {
            Entity entity = Entity.getEntity(body);
            if (entity == null) return false;
            if (!(entity instanceof Grabbable)) return false;
            if (body.getPosition().dst(cursorPosition) > grabRadius) return false;
            return true;
        });
        if (grabbed == null) return;

        Vector2 grabPoint = FindBody.closestPoint(grabbed, cursorPosition);

        DistanceJointDef distJointDef = new DistanceJointDef();
        distJointDef.initialize(this.b, grabbed, this.b.getPosition(), grabPoint);
        distJointDef.dampingRatio = 0.7f;
        distJointDef.frequencyHz = 5;
        distJointDef.collideConnected = true;
        distJointDef.length = getCursorDistance();
        grabDistanceJoint = (DistanceJoint) this.world.createJoint(distJointDef);
        grabDistanceJoint.setUserData(new JointDisplay(false));

        MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(grabPoint);
        mouseJointDef.bodyA = this.b;
        mouseJointDef.bodyB = grabbed;
        mouseJointDef.maxForce = maxGrabForce;
        grabMouseJoint = (MouseJoint) this.world.createJoint(mouseJointDef);
        grabMouseJoint.setUserData(new JointDisplay(new Color(1, 1, 1, 1)));

        // TODO: make some joints hidden (make custom joint class)
        //  joint.setUserData();

        MassData massData = grabbed.getMassData();
        massData.mass /= 100;
        grabbed.setMassData(massData);

        grabbedEntity = (BlockEntity) grabbed.getUserData();
        grabbedEntity.grabber = this;
    }

    public void put() {
        if (grabDistanceJoint == null) return;
        grabbedEntity.grabber = null;
        grabbedEntity.b.resetMassData();
        grabbedEntity = null;
        GameScreen.removeJoint(grabDistanceJoint);
        grabDistanceJoint = null;
        GameScreen.removeJoint(grabMouseJoint);
        grabMouseJoint = null;
    }

    public void throwGrabbed() {
        if (grabDistanceJoint == null) return;
        grabbedEntity.b.resetMassData();
        grabbedEntity.b.applyLinearImpulse(cursorDirection.cpy().scl(throwPower), grabDistanceJoint.getAnchorB(), true);
        this.b.applyLinearImpulse(cursorDirection.cpy().scl(-throwPower / 4), this.b.getPosition(), true);
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
    public void remove() {
        put();
        super.remove();
        controller.unregister();
    }

    @Override
    public boolean render(ShapeRenderer renderer) {
        super.render(renderer);
        float cursorDistance = getCursorDistance();
        renderer.setColor(1, 1, 1, 0.5f);

        Item selectedItem = inventory.getSelectedItem();

        float angle = cursorDirection.angleDeg();
        rotation(renderer, angle, () -> {
            renderer.line(0, 0, cursorDistance, 0);
            translation(renderer, cursorDistance, 0, () -> {
                renderer.circle(0, 0, 1.2f);
                if (mode == Mode.INV && selectedItem != null) {
                    selectedItem.render(renderer, this);
                }
            });
        });

        if (mode == Mode.INV) {
            translation(renderer, 0, -7f, () -> {
                inventory.renderPart(renderer, 3);
            });
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
