package io.github._20nickname20.imbored;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.entities.InventoryHolder;
import io.github._20nickname20.imbored.entities.damagable.living.human.CursorEntity;

public abstract class Item {
    private final float size;
    private boolean isUpdating = false;
    private Entity holder;
    private boolean isSelected = false;

    public Item(Entity holder, float size) {
        this.size = size;
        this.holder = holder;
    }

    public void setHolder(Entity holder) {
        this.holder = holder;
    }

    public abstract void render(ShapeRenderer renderer, CursorEntity handHolder);

    public final boolean isUpdating() {
        return this.isUpdating;
    }

    public final void setUpdating(boolean isUpdating) {
        this.isUpdating = isUpdating;
    }

    public void update(InventoryHolder holder, float dt) {

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

    public void onRemove(InventoryHolder holder) {

    }

    public final Vector2 getPosition() {
        if (isSelected && holder instanceof CursorEntity cursorEntity) {
            return cursorEntity.getCursorPosition();
        }
        return holder.b.getPosition();
    }
}
