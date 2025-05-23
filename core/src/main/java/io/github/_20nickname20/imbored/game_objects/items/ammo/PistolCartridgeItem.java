package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;

public class PistolCartridgeItem extends AmmoCartridgeItem {
    private static final float SIZE = 0.3f;
    private static final int MAX_AMMO = 12;
    private static final Color BASE_COLOR = Color.GRAY;
    private static final Color AMMO_COLOR = Color.ORANGE;

    public PistolCartridgeItem(int initAmmo) {
        super(initAmmo);
    }

    public PistolCartridgeItem() {
        this(MAX_AMMO);
    }

    public PistolCartridgeItem(ItemData data) {
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
