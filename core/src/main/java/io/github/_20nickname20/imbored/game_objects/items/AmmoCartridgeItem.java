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
    public final int maxAmmo;

    public AmmoCartridgeItem(Entity holder, float size, int ammo, int maxAmmo, Color baseColor, Color ammoColor) {
        super(holder, size);
        this.ammo = ammo;
        this.maxAmmo = maxAmmo;
        this.baseColor = baseColor;
        this.ammoColor = ammoColor;
    }

    private final Color baseColor, ammoColor;

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        With.rotation(renderer, -20f, () -> {
            renderer.setColor(baseColor);
            renderer.rect(-0.6f, -1, 0.8f, 2);
            renderer.rect(-0.5f, -0.6f, 0.6f, 1);
            renderer.setColor(ammoColor);
            renderer.rect(-0.65f, 1, 0.9f, 0.1f);
        });
    }

}
