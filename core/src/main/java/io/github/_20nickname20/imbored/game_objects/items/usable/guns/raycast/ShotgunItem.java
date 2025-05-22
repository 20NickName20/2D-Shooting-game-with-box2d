package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.ShotgunCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.RaycastGunItem;
import io.github._20nickname20.imbored.render.GameRenderer;

public class ShotgunItem extends RaycastGunItem {
    private static final float SIZE = 2f;
    private static final float RANGE = 80f;
    private static final float DAMAGE = 5.5f;
    private static final float POWER = 10f;
    private static final float RECOIL = 6f;
    private static final float MAX_SCATTER_ANGLE = MathUtils.degRad * 5.2f;
    private static final float REQUIRED_RELOAD_TIME = 7.3f;
    private static final int MAX_AMMO = 5;
    private static final float COOLDOWN = 0.65f;
    private static final Class<? extends AmmoCartridgeItem> AMMO_TYPE = ShotgunCartridgeItem.class;
    private static final float RAY_LENGTH = 74f;
    private static final float RAY_SPEED = 70;
    private static final Color RAY_COLOR = new Color(0.6f, 0.6f, 0.5f, 1);
    private static final float PENETRATE_AMOUNT = 0.8f;

    public ShotgunItem(int ammo) {
        super(ammo);
    }

    public ShotgunItem() {
        this(MAX_AMMO);
    }

    public ShotgunItem(ItemData data) {
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
    protected void onShoot(PlayerEntity player) {
        for (int i = -2; i <= 2; i++) {
            shootRay(player, (float) (Math.PI / 48f) * i);
        }
        this.useAmmo();
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        shootAttempt(player);
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);

        renderer.setColor(Material.WOOD.color);
        renderer.rect(-1.8f, -0.55f, 1.4f, 0.6f);
        renderer.rect(-1.2f, -0.2f, 3.5f, 1.1f);
    }
}
