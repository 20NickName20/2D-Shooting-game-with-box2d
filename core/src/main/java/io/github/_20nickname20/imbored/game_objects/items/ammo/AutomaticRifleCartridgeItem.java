package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.util.With;

public class AutomaticRifleCartridgeItem extends AmmoCartridgeItem {
    private static final float SIZE = 0.3f;
    private static final int MAX_AMMO = 47;
    private static final Color BASE_COLOR = new Color(0.3f, 0.5f, 0.3f, 1);
    private static final Color AMMO_COLOR = new Color(0.6f, 0.5f, 0.0f, 1);

    public AutomaticRifleCartridgeItem(int initAmmo) {
        super(initAmmo);
    }

    public AutomaticRifleCartridgeItem() {
        this(MAX_AMMO);
    }

    public AutomaticRifleCartridgeItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return SIZE;
    }
    @Override
    public int getMaxAmmo() {
        return MAX_AMMO;
    }
    @Override
    public Color getBaseColor() {
        return BASE_COLOR;
    }
    @Override
    public Color getAmmoColor() {
        return AMMO_COLOR;
    }
}
