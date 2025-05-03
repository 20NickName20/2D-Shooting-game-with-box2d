package io.github._20nickname20.imbored.game_objects.items.usable;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;

public abstract class BaseGunItem extends UsableItem {
    protected float cooldown, damage;
    protected int ammo;
    protected final int maxAmmo;
    protected float lastShootTime = 0;
    // TODO: add reloading (and ammo)
    public BaseGunItem(Entity holder, float size, float cooldown, float damage, int ammo, int maxAmmo) {
        super(holder, size);
        this.cooldown = cooldown;
        this.damage = damage;
        this.ammo = ammo;
        this.maxAmmo = maxAmmo;
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
        ammo -= 1;
    }
}
