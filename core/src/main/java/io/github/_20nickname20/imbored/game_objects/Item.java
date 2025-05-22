package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.With;

public abstract class Item implements Removable {
    private Entity holder;
    private boolean isRemoved = false;
    protected boolean isEquipped = false;

    protected ItemData persistentData;

    public Item() {

    }

    public Item(ItemData data) {
        this.persistentData = data;
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

    public abstract void render(GameRenderer renderer, CursorEntity handHolder);

    protected final void withNoRotation(GameRenderer renderer, CursorEntity handHolder, Runnable runnable) {
        if (handHolder == null) return;
        renderer.withRotation(-handHolder.getCursorDirection().angleDeg(), runnable);
    }

    protected void renderBar(GameRenderer renderer, CursorEntity handHolder, float yTranslation, BarDisplay barDisplay) {
        if (handHolder == null) return;
        renderer.withRotation(-handHolder.getCursorDirection().angleDeg(), () -> {
            renderer.withTranslation(0, yTranslation, () -> {
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

    public abstract float getSize();

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
        if (holder == null) return Vector2.Zero;
        if (isEquipped && holder instanceof CursorEntity cursorEntity) {
            return cursorEntity.getCursorPosition();
        }
        return holder.b.getPosition();
    }

    public boolean canPickup() {
        return true;
    }

    public ItemData createPersistentData() {
        if (persistentData == null) {
            persistentData = new ItemData();
        }

        persistentData.className = this.getClass().getName();
        return persistentData;
    }

    public static Item createFromType(Class<? extends Item> typeClass, Entity holder) {
        try {
            Item item = typeClass.getConstructor().newInstance();
            item.setHolder(holder);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Item createFromData(ItemData data, Entity holder) {
        try {
            Class<?> clazz = Class.forName(data.className);
            Item item = (Item) clazz.getConstructor(ItemData.class).newInstance(data);
            item.setHolder(holder);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class ItemData {
        String className;
    }
}
