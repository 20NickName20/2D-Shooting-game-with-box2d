package io.github._20nickname20.imbored.game_objects.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.With;

public abstract class AmmoCartridgeItem extends Item {
    public int ammo;

    public AmmoCartridgeItem(int ammo) {
        this.ammo = ammo;
    }

    public AmmoCartridgeItem(ItemData data) {
        super(data);
        if (data instanceof AmmoItemData ammoData) {
            this.ammo = ammoData.ammo;
        }
    }

    public abstract int getMaxAmmo();
    public abstract Color getBaseColor();
    public abstract Color getAmmoColor();

    @Override
    public ItemData createPersistentData() {
        AmmoItemData ammoData = new AmmoItemData();
        ammoData.ammo = this.ammo;
        this.persistentData = ammoData;
        return super.createPersistentData();
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        With.rotation(renderer, -20f, () -> {
            renderer.setColor(getBaseColor());
            renderer.rect(-0.6f, -1, 0.8f, 2);
            renderer.rect(-0.5f, -0.6f, 0.6f, 1);
            renderer.setColor(getAmmoColor());
            renderer.rect(-0.65f, 1, 0.9f, 0.1f);
        });

        this.withNoRotation(renderer, handHolder, () -> {
            With.translation(renderer, 0, 3, () -> {
                BarDisplay.render(renderer, Color.CORAL, Color.LIGHT_GRAY, (float) ammo / (float) getMaxAmmo());
            });
        });
    }

    public static class AmmoItemData extends ItemData {
        int ammo;
    }
}
