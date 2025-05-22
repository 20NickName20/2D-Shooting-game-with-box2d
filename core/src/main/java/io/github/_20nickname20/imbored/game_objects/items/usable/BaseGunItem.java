package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;

public abstract class BaseGunItem extends UsableItem {
    private int ammo;
    private float reloadTime = 0;
    protected float lastShootTime = 0;

    private final BarDisplay ammoBar = new BarDisplay(Color.DARK_GRAY, Color.LIGHT_GRAY, 1, 0.9f);

    public BaseGunItem(int ammo) {
        super();
        this.ammo = ammo;
    }

    public BaseGunItem(ItemData data) {
        super(data);
        if (data instanceof GunItemData gunData) {
            this.ammo = gunData.ammo;
        }
    }

    public abstract float getDamage();
    public abstract float getCooldown();
    public abstract int getMaxAmmo();
    public abstract float getRequiredReloadTime();
    public abstract Class<? extends AmmoCartridgeItem> getAmmoType();

    private BarDisplay reloadBar = new BarDisplay(Color.LIGHT_GRAY, Color.CORAL, 0, 0.5f);
    protected AmmoCartridgeItem reloadingAmmo = null;

    public void reload(AmmoCartridgeItem ammoCartridge) {
        this.ammo += ammoCartridge.ammo;
        if (this.ammo > getMaxAmmo()) {
            int rest = this.ammo - getMaxAmmo();
            this.ammo = getMaxAmmo();
            ammoCartridge.ammo = rest;
        } else {
            ammoCartridge.ammo = 0;
        }
        ammoBar.setTargetValue((float) ammo / (float) getMaxAmmo());
    }

    @Override
    public void onOtherAppliedStart(Item other) {
        super.onOtherAppliedStart(other);
        if (ammo >= getMaxAmmo()) return;
        if (other instanceof AmmoCartridgeItem ammoCartridge) {
            if (ammoCartridge.ammo <= 0) return;
            if (!getAmmoType().isInstance(ammoCartridge)) return;
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
        return Util.time() - lastShootTime > getCooldown();
    }

    public final void shootAttempt(PlayerEntity player) {
        if (ammo == 0) ammoBar.setTargetValue(0);
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
            reloadBar.setTargetValue(reloadTime / getRequiredReloadTime());

            if (reloadTime >= getRequiredReloadTime()) {
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
        ammoBar.setTargetValue((float) ammo / (float) getMaxAmmo());
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        this.renderBar(renderer, handHolder, -5f, ammoBar);
        this.renderBar(renderer, handHolder, -7f, reloadBar);
    }

    @Override
    public ItemData createPersistentData() {
        GunItemData gunItemData;
        if (this.persistentData == null) {
            gunItemData = new GunItemData();
        } else {
            gunItemData = (GunItemData) this.persistentData;
        }
        gunItemData.ammo = this.ammo;
        this.persistentData = gunItemData;
        return super.createPersistentData();
    }

    public static class GunItemData extends ItemData {
        int ammo;
    }
}
