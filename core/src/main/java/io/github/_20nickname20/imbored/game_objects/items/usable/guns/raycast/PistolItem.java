package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.PistolCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.RaycastGunItem;

public class PistolItem extends RaycastGunItem {
    private static final float SIZE = 1.5f;
    private static final float RANGE = 250f;
    private static final float DAMAGE = 7.5f;
    private static final float POWER = 50f;
    private static final float RECOIL = 4f;
    private static final float MAX_SCATTER_ANGLE = MathUtils.degRad * 2f;
    private static final float REQUIRED_RELOAD_TIME = 4f;
    private static final int MAX_AMMO = 12;
    private static final float COOLDOWN = 0.2f;
    private static final Class<? extends AmmoCartridgeItem> AMMO_TYPE = PistolCartridgeItem.class;
    private static final float RAY_LENGTH = 51.5f;
    private static final float RAY_SPEED = 100;
    private static final Color RAY_COLOR = Color.GRAY;

    public PistolItem(int ammo) {
        super(ammo);
    }

    public PistolItem() {
        this(MAX_AMMO);
    }

    public PistolItem(ItemData data) {
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
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        renderer.rectLine(-0.9f, -0.9f, 0.1f, 0.1f, 1.1f);
        renderer.polygon(new float[]{
            0f, 0.5f,
            2f, 0.9f,
            2f, -0.9f,
            0f, -0.5f,
        });
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        shootAttempt(player);
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }
}
