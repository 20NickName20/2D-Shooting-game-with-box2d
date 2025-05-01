package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;

public abstract class Item {
    private final float size;
    private Entity holder;
    private boolean isRemoved = false;
    private boolean isSelected = false;

    public Item(Entity holder, float size) {
        this.holder = holder;
        this.size = size;
    }

    public void setHolder(Entity holder) {
        this.holder = holder;
    }

    public Entity getHolder() {
        return holder;
    }

    public abstract void render(ShapeRenderer renderer, CursorEntity handHolder);

    public void update(float dt) {

    }

    public void remove() {
        isRemoved = true;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public final float getSize() {
        return size;
    }

    public void onSelect(InventoryHolder holder) {
        isSelected = true;
    }

    public void onDeselect(InventoryHolder holder) {
        isSelected = false;
    }

    public void onPickup(PlayerEntity holder) {

    }

    public final Vector2 getPosition() {
        if (isSelected && holder instanceof CursorEntity cursorEntity) {
            return cursorEntity.getCursorPosition();
        }
        return holder.b.getPosition();
    }

    public static Item createFromType(Class<? extends Item> typeClass, Entity holder) {
        try {
            return typeClass.getConstructor(Entity.class).newInstance(holder);
        } catch (Exception e) {
            return null;
        }
    }
}
