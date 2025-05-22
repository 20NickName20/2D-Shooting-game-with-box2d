package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.SniperRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.RaycastGunItem;
import io.github._20nickname20.imbored.render.GameRenderer;

public class SniperRifleGunItem extends RaycastGunItem {
    private static final float SIZE = 3.3f;
    private static final float RANGE = 320f;
    private static final float DAMAGE = 40.2874f;
    private static final float POWER = 65f;
    private static final float RECOIL = 12f;
    private static final float MAX_SCATTER_ANGLE = MathUtils.degRad * 1;
    private static final float REQUIRED_RELOAD_TIME = 10f;
    private static final int MAX_AMMO = 5;
    private static final float COOLDOWN = 1.2f;
    private static final Class<? extends AmmoCartridgeItem> AMMO_TYPE = SniperRifleCartridgeItem.class;
    private static final float RAY_LENGTH = 120f;
    private static final float RAY_SPEED = 130;
    private static final Color RAY_COLOR = new Color(0.7f, 0.5f, 0.5f, 1);
    private static final float PENETRATE_AMOUNT = 2f;

    public SniperRifleGunItem(int ammo) {
        super(ammo);
    }

    public SniperRifleGunItem() {
        super(MAX_AMMO);
    }

    public SniperRifleGunItem(ItemData data) {
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
        renderer.setColor(0.3f, 0.5f, 0.2f, 1);

        renderer.withRotation(30f, () -> {
            renderer.rect(-0.7f, -0.3f, 0.7f, 0.6f);
        });
        renderer.rect(0, -0.25f, 4f, 0.55f);
        renderer.rect(0.3f, 0.3f, 3f, 0.3f);
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        shootAttempt(player);
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }
}
