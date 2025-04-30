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

    public boolean canPickup() {
        if (isRemoved()) return false;
        return Util.time() - this.spawnTime > PICKUP_DELAY;
    }

    @Override
    public boolean shouldCollide(Entity other) {
        if (other instanceof InteractiveContainerEntity) {
            return false;
        }
        return true;
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
