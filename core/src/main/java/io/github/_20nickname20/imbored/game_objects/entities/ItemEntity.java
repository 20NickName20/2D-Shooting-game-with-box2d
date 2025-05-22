package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.container.InteractableContainerEntity;
import io.github._20nickname20.imbored.game_objects.items.scrap.MetalScrapItem;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Shapes;
import io.github._20nickname20.imbored.util.Util;

public class ItemEntity extends BlockEntity {
    public final Item item;
    public static float PICKUP_DELAY = 0.5f;

    private float timeBeforeDespawn = 2f * 60f;

    public ItemEntity(GameWorld world, float x, float y, Item item) {
        super(world, x, y, Shapes.circleShape(1f));
        this.item = item;
        item.setHolder(this);
    }

    public ItemEntity(GameWorld world, EntityData data) {
        super(world, data);
        if (data instanceof ItemEntityData itemEntityData) {
            this.item = Item.createFromData(itemEntityData.itemData, this);
        } else {
            this.item = new MetalScrapItem();
        }
    }

    @Override
    public float getImpenetrability() {
        return 0f;
    }

    @Override
    public Material getMaterial() {
        return Material.ITEM;
    }

    public void setIgnoreDelay() {
        ignoreDelay = true;
    }

    private boolean ignoreDelay = false;

    public boolean canPickup() {
        if (isRemoved()) return false;
        if (!ignoreDelay && Util.time() - this.spawnTime < PICKUP_DELAY) return false;
        return item.canPickup();
    }

    @Override
    public boolean shouldCollide(Entity other) {
        if (Util.time() - this.spawnTime < PICKUP_DELAY && other instanceof InteractableContainerEntity) {
            return false;
        }
        if (other instanceof ItemEntity) {
            return false;
        }
        return true;
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        item.update(dt);
        if (item.isRemoved()) {
            this.remove();
            return;
        }
        timeBeforeDespawn -= dt;
        if (timeBeforeDespawn <= 0) {
            this.remove();
        }
    }

    @Override
    public EntityData createPersistentData() {
        ItemEntityData data;
        if (this.persistentData == null) {
            data = new ItemEntityData();
        } else {
            data = (ItemEntityData) this.persistentData;
        }
        data.itemData = item.createPersistentData();
        this.persistentData = data;
        return super.createPersistentData();
    }

    @Override
    public boolean render(GameRenderer renderer) {
        if (timeBeforeDespawn < 20f) {
            if (Math.sin((20 - timeBeforeDespawn) * (20 - timeBeforeDespawn)) < 0) {
                return true;
            }
        }
        renderer.withRotation(this.b.getAngle() * MathUtils.radiansToDegrees, () -> {
            item.render(renderer, null);
        });
        return true;
    }

    public static class ItemEntityData extends DamagableEntityData {
        Item.ItemData itemData;
    }
}
