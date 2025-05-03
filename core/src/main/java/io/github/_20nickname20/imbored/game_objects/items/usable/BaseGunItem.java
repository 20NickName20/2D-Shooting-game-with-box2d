package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;

public abstract class BaseGunItem extends UsableItem {
    protected float cooldown, damage;
    private int ammo;
    private float reloadTime = 0;
    private final float requiredReloadTime;
    protected final int maxAmmo;
    protected float lastShootTime = 0;
    private final Class<? extends AmmoCartridgeItem> ammoTypeClass;

    private final BarDisplay ammoBar = new BarDisplay(Color.DARK_GRAY, Color.LIGHT_GRAY, 1, 0.9f);

    public BaseGunItem(Entity holder, float size, float cooldown, float damage, int ammo, int maxAmmo, float requiredReloadTime, Class<? extends AmmoCartridgeItem> ammoTypeClass) {
        super(holder, size);
        this.cooldown = cooldown;
        this.damage = damage;
        this.ammo = ammo;
        this.requiredReloadTime = requiredReloadTime;
        this.maxAmmo = maxAmmo;
        this.ammoTypeClass = ammoTypeClass;
    }

    private BarDisplay reloadBar = new BarDisplay(Color.LIGHT_GRAY, Color.CORAL, 0, 0.5f);
    protected AmmoCartridgeItem reloadingAmmo = null;

    public void reload(AmmoCartridgeItem ammoCartridge) {
        this.ammo += ammoCartridge.ammo;
        if (this.ammo > maxAmmo) {
            int rest = this.ammo - maxAmmo;
            this.ammo = maxAmmo;
            ammoCartridge.ammo = rest;
        } else {
            ammoCartridge.ammo = 0;
        }
        ammoBar.setTargetValue((float) ammo / (float) maxAmmo);
    }

    @Override
    public void onOtherAppliedStart(Item other) {
        super.onOtherAppliedStart(other);
        if (ammo >= maxAmmo) return;
        if (other instanceof AmmoCartridgeItem ammoCartridge) {
            if (ammoCartridge.ammo <= 0) return;
            if (!ammoTypeClass.isInstance(ammoCartridge)) return;
            reloadingAmmo = ammoCartridge;
        }
    }

    @Override
    public void onOtherAppliedStop(Item other) {
        super.onOtherAppliedStop(other);
        reloadingAmmo = null;
        reloadTime = 0;
        reloadBar.setTargetValue(0);
    }

    public boolean canShoot() {
        if (ammo <= 0) return false;
        return Util.time() - lastShootTime > cooldown;
    }

    public final void shootAttempt(PlayerEntity player) {
        if (!canShoot()) return;
        lastShootTime = Util.time();
        onShoot(player);
    }

    protected void onShoot(PlayerEntity player) {
        useAmmo();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        ammoBar.update(dt);

        if (reloadingAmmo != null) {
            reloadTime += dt;
            reloadBar.setTargetValue(reloadTime / requiredReloadTime);

            if (reloadTime >= requiredReloadTime) {
                reload(reloadingAmmo);
                reloadingAmmo = null;
                reloadTime = 0;
                reloadBar.setTargetValue(0);
            }
        }
        reloadBar.update(dt);
    }

    protected void useAmmo() {
        ammo -= 1;
        ammoBar.setTargetValue((float) ammo / (float) maxAmmo);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        this.renderBar(renderer, handHolder, -5f, ammoBar);
        this.renderBar(renderer, handHolder, -7f, reloadBar);
    }
}
