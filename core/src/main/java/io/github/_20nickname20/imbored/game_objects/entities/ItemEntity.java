package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.container.InteractiveContainerEntity;
import io.github._20nickname20.imbored.util.Shapes;
import io.github._20nickname20.imbored.util.Util;

public class ItemEntity extends BlockEntity {
    public final Item item;
    public static float PICKUP_DELAY = 0.5f;

    public ItemEntity(GameWorld world, float x, float y, Item item) {
        super(world, x, y, Shapes.boxShape(1f, 1f), Material.ITEM, 500);
        this.item = item;
        item.setHolder(this);
    }

    public void setIgnoreDelay() {
        ignoreDelay = true;
    }

    private boolean ignoreDelay = false;

    public boolean canPickup() {
        if (isRemoved()) return false;
        if (ignoreDelay) return true;
        return Util.time() - this.spawnTime > PICKUP_DELAY;
    }

    @Override
    public boolean shouldCollide(Entity other) {
        if (other instanceof InteractiveContainerEntity) {
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
    }

    @Override
    public void onSpawn(World world) {
        super.onSpawn(world);
        this.b.setFixedRotation(true);
    }

    @Override
    public boolean render(ShapeRenderer renderer) {
        item.render(renderer, null);
        return true;
    }
}
