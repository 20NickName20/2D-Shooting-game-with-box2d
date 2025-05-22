package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.PistolCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.AutomaticRaycastGunItem;
import io.github._20nickname20.imbored.render.GameRenderer;

public class AutomaticRifleItem extends AutomaticRaycastGunItem {
    private static final float SIZE = 2f;
    private static final float RANGE = 150f;
    private static final float DAMAGE = 4f;
    private static final float POWER = 10f;
    private static final float RECOIL = 1f;
    private static final float MAX_SCATTER_ANGLE = MathUtils.degRad * 8f;
    private static final float REQUIRED_RELOAD_TIME = 10f;
    private static final int MAX_AMMO = 47;
    private static final float COOLDOWN = 0.075f;
    private static final Class<? extends AmmoCartridgeItem> AMMO_TYPE = PistolCartridgeItem.class;
    private static final float RAY_LENGTH = 43f;
    private static final float RAY_SPEED = 110f;
    private static final Color RAY_COLOR = Color.GRAY;
    private static final float PENETRATE_AMOUNT = 1f;

    public AutomaticRifleItem(int ammo) {
        super(ammo);
    }

    public AutomaticRifleItem() {
        this(MAX_AMMO);
    }

    public AutomaticRifleItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return SIZE;
    }
    @Override
    public float getRange() {
        return RANGE;
    }
    @Override
    public float getDamage() {
        return DAMAGE;
    }
    @Override
    public float getPower() {
        return POWER;
    }
    @Override
    public float getRecoil() {
        return RECOIL;
    }
    @Override
    public float getMaxScatterAngle() {
        return MAX_SCATTER_ANGLE;
    }
    @Override
    public float getRequiredReloadTime() {
        return REQUIRED_RELOAD_TIME;
    }
    @Override
    public int getMaxAmmo() {
        return MAX_AMMO;
    }
    @Override
    public float getCooldown() {
        return COOLDOWN;
    }
    @Override
    public Class<? extends AmmoCartridgeItem> getAmmoType() {
        return AMMO_TYPE;
    }
    @Override
    public float getRayLength() {
        return RAY_LENGTH;
    }
    @Override
    public float getRaySpeed() {
        return RAY_SPEED;
    }
    @Override
    public Color getRayColor() {
        return RAY_COLOR;
    }

    @Override
    public float getPenetrateAmount() {
        return PENETRATE_AMOUNT;
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        renderer.withRotation(20f, () -> {
            renderer.rect(-1f, -0.1f, 1.3f, 0.5f);
        });
        renderer.rect(0, 0, 3.2f, 1.11111f);
    }
}
