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
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.render.JointDisplay;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.List;

import static io.github._20nickname20.imbored.util.With.translation;

public class PlayerEntity extends CursorEntity implements InventoryHolder {
    private float food = 100;
    private final float maxFood = 100;
    private BarDisplay foodBar = new BarDisplay(new Color(0.6f, 0.2f, 0, 1), new Color(0.9f, 0.8f, 0, 1), 1, 1);

    private float thirst = 100;
    private final float maxThirst = 100;

    private float infection;

    PlayerController controller;
    private float lastJumpTime = 0;

    private final float maxGrabForce = 200;
    private final float grabRadius = 6;
    private final float throwPower = 55;

    private final float itemDropPower = 15;

    private final float handCursorDistance = 8;

    private Entity grabbedEntity;
    private InteractiveContainerEntity currentContainer;
    private MouseJoint grabMouseJoint;
    private DistanceJoint grabDistanceJoint;

    private Item equippedItem = null;

    public boolean popBob = false;

    public final Inventory inventory = new Inventory(this, 20);

    public float customColor = MathUtils.random();

    public PlayerEntity(GameWorld world, float x, float y, PlayerController controller) {
        super(world, x, y, 100, 20);
        this.setCursorDistance(getHandCursorDistance());
        this.controller = controller;
        controller.register(this);
    }

    public void equipSelectedItem() {
        Item item = inventory.getSelectedItem();
        if (item == null) return;
        if (equippedItem != null) {
            equippedItem.onUnequip(this);
        } else {
            put();
        }
        item.onEquip(this);
        equippedItem = inventory.getSelectedItem();
        this.setCursorDistance(equippedItem.getCursorDistance());
    }

    public Item getEquippedItem() {
        return equippedItem;
    }

    public void unequipItem() {
        if (equippedItem == null) return;
        equippedItem.onUnequip(this);
        equippedItem = null;
        this.setCursorDistance(handCursorDistance);
    }

    @Override
    public float getHandCursorDistance() {
        return handCursorDistance;
    }

    @Override
    public void onDestroy() {
        if (this.isRemoved()) return;
        PlayerEntity newPlayer = new PlayerEntity(gameWorld, gameWorld.camera.position.x, -20, controller);
        newPlayer.customColor = this.customColor + 0.1f;
        gameWorld.spawn(newPlayer);
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
        if (isApplyingSelectedToEquipped) {
            stopApplyingSelectedToEquipped();
        }
        inventory.scroll(amount);
    }

    private float lastPopBobTime = 0;
    @Override
    public void update(float dt) {
        super.update(dt);
        inventory.update(dt);
        if (equippedItem != null && equippedItem.isRemoved()) {
            unequipItem();
        }
        controller.update(dt);

        foodBar.update(dt);

        Vector2 cursorPosition = getCursorPosition();
        float time = Util.time();

        if (popBob && time - lastPopBobTime > 0.01f) {
            lastPopBobTime = time;
            gameWorld.dropItem(cursorPosition, this.getCursorDirection().scl(itemDropPower * 5).rotateDeg(MathUtils.random(-20f, 20f)), new TestRandomLoot().generate(1).get(0));
        }

        if (grabbedEntity != null) {
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

        Vector2 position = this.b.getPosition();
        Body closest = FindBody.closestFiltered(world, position, (body) -> {
            if (!(body.getUserData() instanceof InteractiveContainerEntity filteredContainer)) return false;
            Vector2 pos = body.getPosition();
            if (pos.cpy().sub(this.b.getPosition()).dot(cursorDirection) < 0) return false;
            if (pos.dst(position) > filteredContainer.getInteractDistance()) return false;
            return true;
        });
        if (closest == null) {
            currentContainer = null;
        } else if (closest.getUserData() instanceof InteractiveContainerEntity container) {
            container.renderInv();
            this.currentContainer = container;
        }
    }

    public void grab() {
        if (equippedItem != null) return;
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
    } // TODO: If not able to grab, then try to equip selected item

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
        grabbedEntity.b.applyLinearImpulse(this.getCursorDirection().scl(throwPower), grabDistanceJoint.getAnchorB(), true);
        this.b.applyLinearImpulse(this.getCursorDirection().scl(-throwPower / 4), this.b.getPosition(), true);
        put();
    }

    public void startUsingItem() {
        if (equippedItem == null) return;
        if (!(equippedItem instanceof UsableItem usable)) return;
        usable.onStartUse(this);
    }

    public void stopUsingItem() {
        if (equippedItem == null) return;
        if (!(equippedItem instanceof UsableItem usable)) return;
        usable.onEndUse(this);
    }

    public void removeSelectedItem() {
        inventory.removeSelectedItem();
    }

    private boolean isApplyingSelectedToEquipped = false;

    public void startApplyingSelectedToEquipped() {
        if (inventory.isEmpty()) return;
        if (equippedItem == null) return;
        Item selectedItem = inventory.getSelectedItem();
        if (selectedItem == equippedItem) return;
        isApplyingSelectedToEquipped = true;

        selectedItem.onApplyToOtherStart(equippedItem);
        equippedItem.onOtherAppliedStart(selectedItem);
    }

    public void stopApplyingSelectedToEquipped() {
        if (!isApplyingSelectedToEquipped) return;
        isApplyingSelectedToEquipped = false;
        Item selectedItem = inventory.getSelectedItem();

        selectedItem.onApplyToOtherStop(equippedItem);
        equippedItem.onOtherAppliedStop(selectedItem);
    }

    public Item removeEquippedItem() {
        if (equippedItem == null) return null;
        inventory.removeItem(equippedItem);
        Item removed = equippedItem;
        unequipItem();
        return removed;
    }

    public ItemEntity dropEquippedItem() {
        Vector2 cursorPosition = this.getCursorPosition();
        Item item = removeEquippedItem();
        if (item == null) return null;
        return gameWorld.dropItem(cursorPosition, this.getCursorDirection().scl(itemDropPower).add(this.b.getLinearVelocity()), item);
    }

    public boolean pickupItem(ItemEntity entity) {
        if (!entity.canPickup()) return false;
        if (!inventory.add(entity.item)) return false;
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
        if (currentContainer == null) return;
        currentContainer.scrollItem(amount);
    }

    public void takeOutOfContainer() {
        if (currentContainer == null) return;
        currentContainer.takeOutSelected(this.b.getPosition().cpy().sub(currentContainer.b.getPosition()).nor().scl(itemDropPower * 2));
    }

    public void putEquippedToContainer() {
        if (equippedItem == null) {
            equipSelectedItem();
        }
        if (currentContainer == null) return;
        if (inventory.isEmpty()) return;
        Item item = removeEquippedItem();
        if (item == null) return;
        if (currentContainer.putItem(item)) return;
        gameWorld.dropItem(getCursorPosition(), this.getCursorDirection().scl(itemDropPower), item);
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

        float angle = cursorDirection.angleDeg();
        With.rotation(renderer, angle, () -> {
            renderer.line(0, 0, cursorDistance, 0);
            translation(renderer, cursorDistance, 0, () -> {
                renderer.circle(0, 0, 1.2f);
                if (equippedItem != null) {
                    equippedItem.render(renderer, this);
                }
            });
        });

        With.translation(renderer, 0, -7f, () -> {
            inventory.renderPart(renderer, 3);
        });

        if (food < 10 || foodBar.isRising()) {
            With.translation(renderer, 0, -8.5f, () -> {
                foodBar.render(renderer);
            });
        }

        renderer.setColor(Util.fromHSV(customColor, 1, 1));
        for (float i = 0; i < 2; i += 0.75f) {
            renderer.rect(-1.75f + i, -3.5f + i, 3.5f - i * 2, 7f - i * 2);
        }
        return true;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Entity getEntity() {
        return this;
    }
}
