package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.util.With;

public class ShotgunCartridgeItem extends AmmoCartridgeItem {
    private static final float SIZE = 0.3f;
    private static final int MAX_AMMO = 6;
    private static final Color BASE_COLOR = new Color(0.5f, 0.1f, 0.0f, 1);
    private static final Color AMMO_COLOR = Color.YELLOW;

    public ShotgunCartridgeItem(int initAmmo) {
        super(initAmmo);
    }

    public ShotgunCartridgeItem() {
        this(6);
    }

    public ShotgunCartridgeItem(ItemData data) {
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
