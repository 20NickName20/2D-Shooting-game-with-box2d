package io.github._20nickname20.imbored.game_objects.entities.container;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.ContainerEntity;
import io.github._20nickname20.imbored.game_objects.entities.ItemEntity;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

public abstract class InteractiveContainerEntity extends ContainerEntity {
    public InteractiveContainerEntity(GameWorld world, float x, float y, Shape shape, Material material, float maxHealth, float inventorySize) {
        super(world, x, y, shape, material, maxHealth, inventorySize);
    }

    public ItemEntity takeOutSelected(Vector2 impulse) {
        if (this.getInventory().isEmpty()) return null;
        Item item = this.getInventory().removeSelectedItem();
        if (item == null) return null;
        ItemEntity itemEntity = gameWorld.dropItem(this.b.getPosition(), impulse, item);
        itemEntity.setIgnoreDelay();
        return itemEntity;
    }

    public boolean putItem(Item item) {
        return getInventory().add(item);
    }

    public static final float INV_RENDER_DISTANCE = 20;

    private float interactTime = 0;

    public float getInteractDistance() {
        return 16;
    }

    public void renderInv() {
        interactTime = Util.time();
    }

    @Override
    public boolean render(ShapeRenderer renderer) {
        super.render(renderer);
        if (Util.time() - interactTime > 0.1f) return false;
        With.translation(renderer, 0, 8, () -> {
            getInventory().renderPart(renderer, 5);
        });
        return false;
    }
}
