package io.github._20nickname20.imbored.game_objects.entities.living.human.cursor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.*;
import io.github._20nickname20.imbored.*;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Inventory;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.*;
import io.github._20nickname20.imbored.game_objects.entities.container.InteractiveContainerEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.BodyEquipableItem;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.List;

import static io.github._20nickname20.imbored.util.With.translation;

public class PlayerEntity extends CursorEntity implements InventoryHolder {
    private static final float MAX_HEALTH = 100f;
    private static final float MAX_FOOD = 100f;
    private static final float MAX_HYDRATION = 100f;
    private static final float DEFAULT_MAX_WALK_SPEED = 20f;

    private float food = 100;
    private BarDisplay foodBar = new BarDisplay(new Color(0.6f, 0.2f, 0, 1), new Color(0.9f, 0.8f, 0, 1), 1, 1);

    private float hydration = 100;

    private float infection;

    PlayerController controller;
    private float lastJumpTime = 0;

    private static final float MAX_GRAB_FORCE = 200;
    private static final float THROW_POWER = 55;
    private static final float GRAB_RADIUS = 6;
    private static final float ITEM_DROP_POWER = 15;
    private static final float HAND_CURSOR_DISTANCE = 8;
    public static final float DEFAULT_INVENTORY_SIZE = 20;

    private BodyEquipableItem bodyEquipped = null;


    private Entity grabbedEntity;
    private InteractiveContainerEntity currentContainer;
    private MouseJoint grabMouseJoint;
    private DistanceJoint grabDistanceJoint;

    private Item equippedItem = null;

    private final Inventory inventory = new Inventory(DEFAULT_INVENTORY_SIZE);

    public float customColor = MathUtils.random();

    public PlayerEntity(GameWorld world, float x, float y, PlayerController controller) {
        super(world, x, y);
        this.setCursorDistance(getHandCursorDistance());
        this.controller = controller;
        this.inventory.setHolder(this);
    }

    public PlayerEntity(GameWorld world, EntityData data) {
        super(world, data);
        remove();
    }

    @Override
    public float getDefaultMaxWalkSpeed() {
        return DEFAULT_MAX_WALK_SPEED;
    }

    @Override
    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    @Override
    public Material getMaterial() {
        return Material.FLESH;
    }

    @Override
    public void onSpawn(World world) {
        super.onSpawn(world);
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
        Item removed = equippedItem;
        equippedItem = null;
        removed.onUnequip(this);
        this.setCursorDistance(HAND_CURSOR_DISTANCE);
    }

    public void unequipBodyItem() {
        if (bodyEquipped == null) return;
        BodyEquipableItem removed = bodyEquipped;
        bodyEquipped = null;
        removed.onBodyUnequip(this);
        if (!inventory.add(bodyEquipped)) {
            dropItem(bodyEquipped);
        }
    }

    public BodyEquipableItem getBodyEquipped() {
        return bodyEquipped;
    }

    public void equipSelectedItemOnBody() {
        Item item = inventory.getSelectedItem();
        if (!(item instanceof BodyEquipableItem bodyEquipable)) return;
        unequipBodyItem();
        bodyEquipable.onBodyEquip(this);
        bodyEquipped = bodyEquipable;
        inventory.removeItem(bodyEquipable);
    }

    @Override
    public float getHandCursorDistance() {
        return HAND_CURSOR_DISTANCE;
    }

    @Override
    public void onDestroy() {
        if (this.isRemoved()) return;
        PlayerEntity newPlayer = new PlayerEntity(gameWorld, gameWorld.camera.position.x, 100, controller);
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

    @Override
    public void update(float dt) {
        super.update(dt);
        inventory.update(dt);
        if (equippedItem != null && equippedItem.isRemoved()) {
            unequipItem();
        }
        if (bodyEquipped != null) {
            bodyEquipped.update(dt);
        }
        controller.update(dt);

        foodBar.update(dt);

        Vector2 cursorPosition = getCursorPosition();

        if (grabbedEntity != null) {
            Vector2 toGrabbed = grabbedEntity.b.getPosition().sub(this.b.getPosition());

            if (toGrabbed.dot(this.getCursorDirection()) < -0.5) {
                put();
                return;
            }
            Vector2 impulse = cursorPosition.cpy().sub(grabbedEntity.b.getPosition());
            float len = impulse.len();
            if (len > GRAB_RADIUS * 2) {
                put();
                return;
            }

            grabMouseJoint.setTarget(cursorPosition);
        }

        Vector2 position = this.b.getPosition();
        Body closest = FindBody.closestFiltered(world, position, (body) -> {
            if (!(body.getUserData() instanceof InteractiveContainerEntity filteredContainer)) return false;
            Vector2 pos = body.getPosition();
            if (pos.cpy().sub(this.b.getPosition()).dot(this.getCursorDirection()) < 0) return false;
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

    public boolean grab() {
        if (equippedItem != null) return true;
        if (grabbedEntity != null) return true;
        Vector2 cursorPosition = this.getCursorPosition();

        Body grabbed = FindBody.closestFiltered(world, cursorPosition, (body) -> {
            Entity entity = Entity.getEntity(body);
            if (entity == null) return false;
            if (!(entity instanceof Grabbable)) return false;
            if (body.getPosition().dst(cursorPosition) > GRAB_RADIUS) return false;
            return true;
        });
        if (grabbed == null) return false;

        Vector2 grabPoint = FindBody.closestPoint(grabbed, cursorPosition);

        DistanceJointDef distJointDef = new DistanceJointDef();
        distJointDef.initialize(this.b, grabbed, this.b.getPosition(), grabPoint);
        distJointDef.dampingRatio = 0.5f;
        distJointDef.frequencyHz = 5f;
        distJointDef.collideConnected = true;
        distJointDef.length = getCursorDistance();
        grabDistanceJoint = (DistanceJoint) this.world.createJoint(distJointDef);
        grabDistanceJoint.setUserData(false);

        MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(grabPoint);
        mouseJointDef.bodyA = this.b;
        mouseJointDef.bodyB = grabbed;
        mouseJointDef.maxForce = MAX_GRAB_FORCE;
        grabMouseJoint = (MouseJoint) this.world.createJoint(mouseJointDef);
        grabMouseJoint.setUserData(Color.WHITE);

        MassData massData = grabbed.getMassData();
        massData.mass /= 500;
        grabbed.setMassData(massData);

        grabbedEntity = (Entity) grabbed.getUserData();
        ((Grabbable) grabbedEntity).onGrabbed(this);
        return true;
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

    public boolean isGrabbed() {
        return grabbedEntity != null;
    }

    public boolean hasContainer() {
        return currentContainer != null;
    }

    public void throwGrabbed() {
        if (grabDistanceJoint == null) return;
        grabbedEntity.b.resetMassData();
        grabbedEntity.b.applyLinearImpulse(this.getCursorDirection().scl(THROW_POWER), grabDistanceJoint.getAnchorB(), true);
        this.b.applyLinearImpulse(this.getCursorDirection().scl(-THROW_POWER / 4), this.b.getPosition(), true);
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

    public void dropItem(Item item) {
        gameWorld.dropItem(this.getCursorPosition(), this.getCursorDirection().scl(ITEM_DROP_POWER).add(this.b.getLinearVelocity()), item);
    }

    public void dropEquippedItem() {
        if (equippedItem == null) {
            equipSelectedItem();
        }
        Item item = removeEquippedItem();
        if (item == null) return;
        dropItem(item);
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
        if (currentContainer == null) {
            scrollItem(amount);
            return;
        }
        currentContainer.scrollItem(amount);
    }

    public void takeOutOfContainer() {
        if (currentContainer == null) return;
        currentContainer.takeOutSelected(this.b.getPosition().cpy().sub(currentContainer.b.getPosition()).nor().scl(ITEM_DROP_POWER * 2));
    }

    public void storeEquippedToContainer() {
        if (equippedItem == null) {
            equipSelectedItem();
        }
        if (currentContainer == null) return;
        if (inventory.isEmpty()) return;
        Item item = removeEquippedItem();
        if (item == null) return;
        if (currentContainer.putItem(item)) return;
        gameWorld.dropItem(getCursorPosition(), this.getCursorDirection().scl(ITEM_DROP_POWER), item);
    }

    @Override
    public void remove() {
        put();
        super.remove();
        if (controller == null) return;
        controller.unregister();
    }

    @Override
    public boolean render(ShapeRenderer renderer) {
        super.render(renderer);
        if (bodyEquipped != null) {
            bodyEquipped.renderOnPlayer(renderer, this);
        }
        float cursorDistance = getCursorDistance();
        renderer.setColor(1, 1, 1, 0.5f);

        float angle = this.getCursorDirection().angleDeg();
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
            inventory.renderPart(renderer, 3, equippedItem);
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

    @Override
    public EntityData createPersistentData() {
        PlayerEntityData playerEntityData;
        if (this.persistentData == null) {
            playerEntityData = new PlayerEntityData();
        } else {
            playerEntityData = (PlayerEntityData) this.persistentData;
        }
        playerEntityData.inventoryData = inventory.createPersistentData();
        if (bodyEquipped != null) {
            playerEntityData.bodyEquippedItem = bodyEquipped.createPersistentData();
        } else {
            playerEntityData.bodyEquippedItem = null;
        }
        playerEntityData.food = this.food;
        playerEntityData.hydration = this.hydration;
        this.persistentData = playerEntityData;
        return super.createPersistentData();
    }

    public static class PlayerEntityData extends DamagableEntityData {
        float food, hydration;
        Inventory.InventoryData inventoryData;
        Item.ItemData bodyEquippedItem;
    }
}
