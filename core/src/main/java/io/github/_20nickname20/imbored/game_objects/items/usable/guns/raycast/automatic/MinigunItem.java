package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.AutomaticRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.AutomaticRaycastGunItem;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.With;

public class MinigunItem extends AutomaticRaycastGunItem {
    private static final float SIZE = 3.5f;
    private static final float RANGE = 120f;
    private static final float DAMAGE = 3f;
    private static final float POWER = 2f;
    private static final float RECOIL = 0.1f;
    private static final float MAX_SCATTER_ANGLE = MathUtils.degRad * 10.5f;
    private static final float REQUIRED_RELOAD_TIME = 15f;
    private static final int MAX_AMMO = 100;
    private static final float COOLDOWN = 0.03f;
    private static final Class<? extends AmmoCartridgeItem> AMMO_TYPE = AutomaticRifleCartridgeItem.class;
    private static final float RAY_LENGTH = 30f;
    private static final float RAY_SPEED = 100;
    private static final Color RAY_COLOR = Color.LIGHT_GRAY;
    private static final float PENETRATE_AMOUNT = 0.9f;

    private float overheat = 0f;

    public MinigunItem(int ammo) {
        super(ammo);
    }

    public MinigunItem() {
        super(MAX_AMMO);
    }

    public MinigunItem(ItemData data) {
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
        if (overheat > 0.8f) {
            return Float.MAX_VALUE;
        }
        return COOLDOWN + COOLDOWN * overheat * 4;
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
    public void update(float dt) {
        super.update(dt);
        if (this.isShooting()) {
            overheat += dt / 10f;
        } else {
            overheat -= dt / 12f;
        }
        overheat = MathUtils.clamp(overheat, 0f, 1.3f);

        if (overheat > 1.29f) {
            this.remove();
            if (this.getHolder() instanceof DamagableEntity damagable) {
                damagable.damage(50f);
                if (damagable instanceof CursorEntity cursorEntity) {
                    cursorEntity.b.applyLinearImpulse(cursorEntity.getCursorDirection().scl(-80), cursorEntity.b.getPosition(), true);
                }
            }
        }
    }

    private final static Color BAR_OUTTER_COLOR = new Color(0.6f, 0.2f, 0, 1);
    private final static Color BAR_INNER_COLOR = new Color(0.97f, 0.55f, 0, 1);

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.6f + overheat / 1.5f, 0.6f - overheat / 1.7f, 0.6f - overheat / 1.6f, 1);
        renderer.withRotation(30f, () -> {
            renderer.rect(-0.5f, -0.35f, 1.3f, 1.1f);
        });
        renderer.rect(-0.1f, -0.1f, 3.4f, 1.65f);

        if (overheat < 0.1f) return;
        if (handHolder == null) return;

        this.withNoRotation(renderer, handHolder, () -> {
            renderer.withTranslation(0, 6f, () -> {
                BarDisplay.render(renderer, BAR_OUTTER_COLOR, BAR_INNER_COLOR, overheat / 1.3f);
            });
        });
    }
}
