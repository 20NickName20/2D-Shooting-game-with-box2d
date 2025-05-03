package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.With;

public abstract class Item implements Removable {
    private final float size;
    private Entity holder;
    private boolean isRemoved = false;
    protected boolean isEquipped = false;

    public Item(Entity holder, float size) {
        this.holder = holder;
        this.size = size;
    }

    public float getCursorDistance() {
        return 4.5f;
    }

    public void setHolder(Entity holder) {
        this.holder = holder;
    }

    public Entity getHolder() {
        return holder;
    }

    public abstract void render(ShapeRenderer renderer, CursorEntity handHolder);

    protected void renderBar(ShapeRenderer renderer, CursorEntity handHolder, float yTranslation, BarDisplay barDisplay) {
        if (handHolder == null) return;
        With.rotation(renderer, -handHolder.cursorDirection.angleDeg(), () -> {
            With.translation(renderer, 0, yTranslation, () -> {
                barDisplay.render(renderer);
            });
        });
    }

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

    public void onEquip(PlayerEntity holder) {
        isEquipped = true;
    }

    public void onUnequip(PlayerEntity holder) {
        isEquipped = false;
    }

    public void onPickup(PlayerEntity holder) {

    }

    public void onApplyToOtherStart(Item other) {

    }

    public void onOtherAppliedStart(Item other) {

    }

    public void onApplyToOtherStop(Item other) {

    }

    public void onOtherAppliedStop(Item other) {

    }

    public final Vector2 getPosition() {
        if (isEquipped && holder instanceof CursorEntity cursorEntity) {
            return cursorEntity.getCursorPosition();
        }
        return holder.b.getPosition();
    }

    public boolean canPickup() {
        return true;
    }

    public static Item createFromType(Class<? extends Item> typeClass, Entity holder) {
        try {
            return typeClass.getConstructor(Entity.class).newInstance(holder);
        } catch (Exception e) {
            return null;
        }
    }
}
