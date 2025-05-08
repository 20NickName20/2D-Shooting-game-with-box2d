package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Inventory;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;

import java.util.List;

public abstract class ContainerEntity extends BlockEntity implements InventoryHolder {
    private final Inventory inventory;

    public ContainerEntity(GameWorld world, float x, float y, Shape shape, float inventorySize) {
        super(world, x, y, shape);
        this.inventory = new Inventory(inventorySize);
        this.inventory.setHolder(this);
    }

    public ContainerEntity(GameWorld world, EntityData data) {
        super(world, data);
        if (data instanceof ContainerEntityData containerEntityData) {
            inventory = new Inventory(containerEntityData.inventoryData);
        } else {
            inventory = new Inventory(0);
        }
        inventory.setHolder(this);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public Entity getEntity() {
        return this;
    }

    public void scrollItem(int amount) {
        inventory.scroll(amount);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        inventory.update(dt);
    }

    @Override
    public List<Item> getDroppedItems() {
        List<Item> items = super.getDroppedItems();
        items.addAll(inventory.getItems());
        return items;
    }

    @Override
    public EntityData createPersistentData() {
        ContainerEntityData containerEntityData;
        if (this.persistentData == null) {
            containerEntityData = new ContainerEntityData();
        } else {
            containerEntityData = (ContainerEntityData) this.persistentData;
        }
        containerEntityData.inventoryData = inventory.createPersistentData();
        this.persistentData = containerEntityData;
        return super.createPersistentData();
    }

    public static class ContainerEntityData extends DamagableEntityData {
        public Inventory.InventoryData inventoryData;
    }
}
