package io.github._20nickname20.imbored.game_objects.entities.living.human.cursor;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;

import io.github._20nickname20.imbored.Controllable;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.PlayerController;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Interact;
import io.github._20nickname20.imbored.game_objects.Inventory;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.Grabbable;
import io.github._20nickname20.imbored.game_objects.entities.Interactable;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.entities.ItemEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.InteractableContainerEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.FindBody;
import io.github._20nickname20.imbored.util.Util;

public class PlayerEntity extends CursorEntity implements InventoryHolder, Controllable {
    private static final float MAX_HEALTH = 100f;
    private static final float DEFAULT_MAX_WALK_SPEED = 20f;

    public PlayerController controller;
    private float lastJumpTime = 0;

    private static final float MAX_GRAB_FORCE = 200;
    private static final float THROW_POWER = 55;
    private static final float GRAB_RADIUS = 6;
    private static final float ITEM_DROP_POWER = 15;
    private static final float HAND_CURSOR_DISTANCE = 8;
    public static final float INVENTORY_SIZE = 20;

    private Entity grabbedEntity;
    private Interactable interactingEntity;
    private MouseJoint grabMouseJoint;

    private Item equippedItem = null;

    private final Inventory inventory;

    public final String username;

    public float customColor = MathUtils.random();

    public PlayerEntity(GameWorld world, float x, float y, PlayerController controller, String username) {
        super(world, x, y);
        this.setCursorDistance(getHandCursorDistance());
        this.controller = controller;

        this.inventory = new Inventory(INVENTORY_SIZE);
        this.inventory.setHolder(this);
        if (username == null) {
            throw new RuntimeException("Username is null!");
        }
        this.username = username;
    }

    public PlayerEntity(GameWorld world, EntityData data) {
        super(world, data);
        this.setCursorDistance(getHandCursorDistance());

        if (data instanceof PlayerEntityData playerEntityData) {
            this.inventory = new Inventory(playerEntityData.inventoryData);
            this.inventory.setHolder(this);
            this.username = playerEntityData.username;
        } else {
            this.inventory = new Inventory(INVENTORY_SIZE);
            this.inventory.setHolder(this);
            this.username = this.uuid.toString();
        }
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

    @Override
    public float getHandCursorDistance() {
        return HAND_CURSOR_DISTANCE;
    }

    @Override
    public void onDestroy() {
        if (this.isRemoved()) return;

        this.unequipItem();

        if (controller != null) {
            this.controller.unregister();
            PlayerEntity newPlayer = new PlayerEntity(gameWorld, 0f, 50f, this.controller, this.username);
            this.controller.register(newPlayer);
            newPlayer.customColor = this.customColor + 0.1f;
            newPlayer.spawn();
            gameWorld.players.put(newPlayer.uuid.toString(), newPlayer);
        }
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

    @Override
    public void interact(Interact interact, boolean isPressed) {
        if (interactingEntity != null) {
            interactingEntity.interact(this, interact, isPressed);
        }
    }

    @Override
    public void dropOrThrow() {
        if (grabbedEntity != null) {
            throwGrabbed();
        } else {
            dropEquippedItem();
        }
    }

    @Override
    public void grabOrUse() {
        if (getEquippedItem() == null) {
            if (!grab()) {
                if (getInventory().getSelectedItem() instanceof UsableItem) {
                    equipSelectedItem();
                }
            }
        } else {
            startUsingItem();
        }
    }

    @Override
    public void putOrStopUse() {
        if (getEquippedItem() == null) {
            put();
        } else {
            stopUsingItem();
        }
    }

    @Override
    public void setCursorAngle(float angle) {
        this.setTarget(Vector2.X.cpy().rotateRad(angle));
    }

    @Override
    public void scrollInventory(int amount) {
        if (isApplyingSelectedToEquipped) {
            stopApplying();
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
        if (controller != null) {
            controller.update(dt);
        }
        
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

        if (position.y < -50) {
            this.damage(100 * dt);
        }

        Body closest = FindBody.closestFiltered(world, position, (body) -> {
            if (!(body.getUserData() instanceof InteractableContainerEntity filteredContainer)) return false;
            Vector2 pos = body.getPosition();
            if (pos.cpy().sub(this.b.getPosition()).dot(this.getCursorDirection()) < 0) return false;
            if (pos.dst(position) > filteredContainer.getInteractDistance()) return false;
            return true;
        });
        if (closest == null) {
            if (interactingEntity != null) {
                interactingEntity.stopInteracting(this);
            }
            interactingEntity = null;
        } else if (closest.getUserData() instanceof Interactable interactable) {
            if (interactingEntity != interactable) {
                if (interactingEntity != null) {
                    interactingEntity.stopInteracting(this);
                }
                interactingEntity = interactable;
                interactingEntity.startInteracting(this);
            }
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

        MouseJointDef mouseJointDef = new MouseJointDef();
        mouseJointDef.collideConnected = true;
        mouseJointDef.target.set(grabPoint);
        mouseJointDef.bodyA = this.b;
        mouseJointDef.bodyB = grabbed;
        mouseJointDef.maxForce = MAX_GRAB_FORCE;
        grabMouseJoint = (MouseJoint) this.world.createJoint(mouseJointDef);
        grabMouseJoint.setUserData(Color.WHITE);

        grabbedEntity = (Entity) grabbed.getUserData();
        ((Grabbable) grabbedEntity).onGrabbed(this);
        return true;
    }

    public void put() {
        if (grabbedEntity == null) return;
        ((Grabbable) grabbedEntity).onPutted(this);
        grabbedEntity = null;
        gameWorld.remove(grabMouseJoint);
        grabMouseJoint = null;
    }

    @Override
    public void toggleItem() {
        if (this.getEquippedItem() == this.getInventory().getSelectedItem()) {
            this.unequipItem();
        } else {
            this.equipSelectedItem();
        }
    }

    public void throwGrabbed() {
        if (grabbedEntity == null) return;
        grabbedEntity.b.applyLinearImpulse(this.getCursorDirection().scl(THROW_POWER), grabMouseJoint.getAnchorB(), true);
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

    public void startApplying() {
        if (inventory.isEmpty()) return;
        if (equippedItem == null) return;
        Item selectedItem = inventory.getSelectedItem();
        if (selectedItem == equippedItem) return;
        isApplyingSelectedToEquipped = true;

        selectedItem.onApplyToOtherStart(equippedItem);
        equippedItem.onOtherAppliedStart(selectedItem);
    }

    public void stopApplying() {
        if (!isApplyingSelectedToEquipped) return;
        isApplyingSelectedToEquipped = false;
        Item selectedItem = inventory.getSelectedItem();

        selectedItem.onApplyToOtherStop(equippedItem);
        equippedItem.onOtherAppliedStop(selectedItem);
    }

    @Override
    public void move(float x) {
        this.setXMovement(x);
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

    @Override
    public void remove() {
        put();
        super.remove();
    }

    @Override
    public boolean render(GameRenderer renderer) {
        super.render(renderer);
        float cursorDistance = getCursorDistance();
        renderer.setColor(1, 1, 1, 0.5f);

        float angle = this.getCursorDirection().angleDeg();
        renderer.withRotation(angle, () -> {
            renderer.line(0, 0, cursorDistance, 0);
            renderer.withTranslation(cursorDistance, 0, () -> {
                renderer.circle(0, 0, 1.2f);
                if (equippedItem != null) {
                    equippedItem.render(renderer, this);
                }
            });
        });

        renderer.withTranslation(0, -7f, () -> {
            inventory.renderPart(renderer, 3, equippedItem);
        });

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
    public Body getBody() {
        return b;
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
        playerEntityData.username = this.username;
        this.persistentData = playerEntityData;
        return super.createPersistentData();
    }

    public static class PlayerEntityData extends DamagableEntityData {
        Inventory.InventoryData inventoryData;
        String username;
    }
}
