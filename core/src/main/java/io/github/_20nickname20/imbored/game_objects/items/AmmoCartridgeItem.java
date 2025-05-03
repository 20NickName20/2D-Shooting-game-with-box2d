package io.github._20nickname20.imbored.game_objects.items;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.render.BarDisplay;

public abstract class AmmoCartridgeItem extends Item {
    public int ammo;
    public final int maxAmmo;

    public AmmoCartridgeItem(Entity holder, float size, int ammo, int maxAmmo) {
        super(holder, size);
        this.ammo = ammo;
        this.maxAmmo = maxAmmo;
    }
}
