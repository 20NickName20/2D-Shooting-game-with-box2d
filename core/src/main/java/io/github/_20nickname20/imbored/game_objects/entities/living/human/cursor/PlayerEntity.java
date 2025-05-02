package io.github._20nickname20.imbored.game_objects.entities.living.human.cursor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.joints.*;
import io.github._20nickname20.imbored.*;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Inventory;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.*;
import io.github._20nickname20.imbored.game_objects.entities.container.InteractiveContainerEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.game_objects.loot.TestRandomLoot;
import io.github._20nickname20.imbored.render.JointDisplay;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.List;

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
    private float itemDropPower = 15;

    private Entity grabbedEntity;
    private InteractiveContainerEntity container;
    private MouseJoint grabMouseJoint;
    private DistanceJoint grabDistanceJoint;

    public boolean popBob = false;

    public final Inventory inventory = new Inventory(this, 20);

    public PlayerEntity(GameWorld world, float x, float y, PlayerController controller) {
        super(world, x, y, 100, 20);
        this.setCursorDistance(getDefaultCursorDistance());
        this.controller = controller;
        controller.register(this);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        this.cursorDistance = getDefaultCursorDistance();
        if (mode != Mode.GRAB) {
            put();
        }
        if (mode != Mode.INV) {
            deselectItem();
            container = null;
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
        gameWorld.spawn(new PlayerEntity(gameWorld, gameWorld.camera.position.x, 50, controller));
        super.onDestroy();
    }

    @Override
    public List<Item> getDroppedItems() {
        List<Item> items = super.getDroppedItems();
        items.addAll(inventory.getItems());
        return items;
    }

    public void jump() {
        if (this.getTimeSinceContact() > 0.2f) return;
        if (Util.time() - lastJumpTime < 0.2f) return;
        if (this.b.getLinearVelocity().y > 20) return;
        if (grabbedEntity != null && this.contacts.size() <= 1 && this.getLastContactedBody() == grabbedEntity.b) return;
        lastJumpTime = Util.time();
        this.b.applyLinearImpulse(new Vector2(0, 50), this.b.getPosition(), true);
    }

    public void scrollItem(int amount) {
        inventory.scroll(amount);
    }

    private float lastPopBobTime = 0;
    @Override
    public void update(float dt) {
        super.update(dt);
        inventory.update(dt);
        controller.update(dt);

        Vector2 cursorPosition = getCursorPosition();
        float time = Util.time();

        if (popBob && time - lastPopBobTime > 0.01f) {
            lastPopBobTime = time;
            gameWorld.dropItem(cursorPosition, cursorDirection.cpy().scl(itemDropPower * 5).rotateDeg(MathUtils.random(-20f, 20f)), new TestRandomLoot().generate(1).get(0));
        }

        if (mode == Mode.GRAB && grabbedEntity != null) {
//            cursorDirection.add(grabbedEntity.b.getLinearVelocity().cpy().scl(dt / 5f));
//            cursorDirection.nor();

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
        if (mode == Mode.INV) {
            Vector2 position = this.b.getPosition();
            Body closest = FindBody.closestFiltered(world, position, (body) -> {
                if (!(body.getUserData() instanceof InteractiveContainerEntity filteredContainer)) return false;
                Vector2 pos = body.getPosition();
                if (pos.cpy().sub(this.b.getPosition()).dot(cursorDirection) < 0) return false;
                if (pos.dst(position) > filteredContainer.getInteractDistance()) return false;
                return true;
            });
            if (closest == null) {
                container = null;
            } else if (closest.getUserData() instanceof InteractiveContainerEntity container) {
                container.renderInv();
                this.container = container;
            }
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
        distJointDef.dampingRatio = 0.5f;
        distJointDef.frequencyHz = 5f;
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

        MassData massData = grabbed.getMassData();
        massData.mass /= 500;
        grabbed.setMassData(massData);

        grabbedEntity = (Entity) grabbed.getUserData();
        ((Grabbable) grabbedEntity).onGrabbed(this);
    }

    public void put() {
        if (grabDistanceJoint == null) return;
        ((Grabbable) grabbedEntity).onPutted(this);
        grabbedEntity.b.resetMassData();
        grabbedEntity = null;
        gameWorld.remove(grabDistanceJoint);
        grabDistanceJoint = null;
        gameWorld.remove(grabMouseJoint);
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

    public ItemEntity dropSelectedItem() {
        if (mode != Mode.INV) return null;
        if (inventory.getSelectedItem() == null) return null;
        deselectItem();
        Vector2 cursorPosition = this.getCursorPosition();
        return gameWorld.dropItem(cursorPosition, this.cursorDirection.cpy().scl(itemDropPower).add(this.b.getLinearVelocity()), inventory.removeSelectedItem());
    }

    public boolean pickupItem(ItemEntity entity) {
        if (!entity.canPickup()) return false;
        if (!inventory.add(entity.item)) return false;
        if (inventory.amount() == 1) {
            entity.item.onSelect(this);
        }
        entity.remove();
        entity.item.onPickup(this);
        return true;
    }

    @Override
    public boolean shouldCollide(Entity other) {
        if (other instanceof PlayerEntity) {
            return false;
        }
        if (other instanceof ItemEntity itemEntity) {
            if (!inventory.canFit(itemEntity.item)) return true;
            pickupItem(itemEntity);
            return false;
        }
        return true;
    }

    public void scrollContainer(int amount) {
        if (container == null) return;
        container.scrollItem(amount);
    }

    public void takeOutOfContainer() {
        if (container == null) return;
        container.takeOutSelected(this.b.getPosition().cpy().sub(container.b.getPosition()).nor().scl(itemDropPower * 2));
    }

    public void putSelectedToContainer() {
        if (container == null) return;
        if (inventory.isEmpty()) return;
        Item item = inventory.removeSelectedItem();
        item.onDeselect(this);
        if (container.putItem(item)) return;
        gameWorld.dropItem(getCursorPosition(), this.cursorDirection.cpy().scl(itemDropPower), item);
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
        With.rotation(renderer, angle, () -> {
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

    @Override
    public Entity getEntity() {
        return this;
    }

    public enum Mode {
        GRAB,
        INV
    }
}
