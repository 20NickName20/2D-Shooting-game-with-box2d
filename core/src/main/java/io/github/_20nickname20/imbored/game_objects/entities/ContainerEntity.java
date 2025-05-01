package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Inventory;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;

import java.util.List;

public abstract class ContainerEntity extends BlockEntity implements InventoryHolder {
    Inventory inventory;

    public ContainerEntity(GameWorld world, float x, float y, Shape shape, Material material, float maxHealth, float inventorySize) {
        super(world, x, y, shape, material, maxHealth);
        this.inventory = new Inventory(this, inventorySize);
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
}
