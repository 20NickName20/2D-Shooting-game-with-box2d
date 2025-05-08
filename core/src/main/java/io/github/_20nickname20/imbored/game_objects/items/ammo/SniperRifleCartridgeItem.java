package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;

public class SniperRifleCartridgeItem extends AmmoCartridgeItem {
    private static final float SIZE = 0.3f;
    private static final int MAX_AMMO = 5;
    private static final Color BASE_COLOR = new Color(0.1f, 0.2f, 0.1f, 1);
    private static final Color AMMO_COLOR = Color.GRAY;

    public SniperRifleCartridgeItem(int initAmmo) {
        super(initAmmo);
    }

    public SniperRifleCartridgeItem() {
        this(MAX_AMMO);
    }

    public SniperRifleCartridgeItem(ItemData data) {
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
