package io.github._20nickname20.imbored.game_objects.entities.container;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Interact;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.ContainerEntity;
import io.github._20nickname20.imbored.game_objects.entities.Interactable;
import io.github._20nickname20.imbored.game_objects.entities.ItemEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Util;

public abstract class InteractableContainerEntity extends ContainerEntity implements Interactable {
    public InteractableContainerEntity(GameWorld world, float x, float y, Shape shape, float inventorySize) {
        super(world, x, y, shape, inventorySize);
    }

    public InteractableContainerEntity(GameWorld world, EntityData data) {
        super(world, data);
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

    public float getInteractDistance() {
        return 16;
    }

    @Override
    public void startInteracting(PlayerEntity player) {
        renderInv = true;
    }

    @Override
    public void stopInteracting(PlayerEntity player) {
        renderInv = false;
    }

    private boolean renderInv = false;

    @Override
    public void interact(PlayerEntity player, Interact interact, boolean state) {
        if (!state) return;
        switch (interact) {
            case LEFT -> scrollItem(-1);
            case RIGHT -> scrollItem(1);
            case DOWN, USE -> {
                Vector2 impulse = player.b.getPosition().cpy().sub(this.b.getPosition()).nor().scl(50f);
                takeOutSelected(impulse);
            }
            case UP -> {
                if (player.getEquippedItem() == null) {
                    player.equipSelectedItem();
                }
                putItem(player.removeEquippedItem());
            }
        }
    }

    @Override
    public boolean render(GameRenderer renderer) {
        super.render(renderer);
        if (!renderInv) return false;
        renderer.withTranslation(0, 8, () -> {
            getInventory().renderPart(renderer, 5);
        });
        return false;
    }
}
