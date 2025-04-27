package io.github._20nickaname20.imbored.items.usable;

import io.github._20nickaname20.imbored.Util;
import io.github._20nickaname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;
import io.github._20nickaname20.imbored.items.UsableItem;

public abstract class GunItem extends UsableItem {
    protected float cooldown, damage;
    protected float lastShootTime = 0;
    // TODO: add reloading
    public GunItem(float size, float cooldown, float damage) {
        super(size);
        this.cooldown = cooldown;
        this.damage = damage;
    }

    public boolean canShoot() {
        return Util.time() - lastShootTime > cooldown;
    }

    public final void shootAttempt(PlayerEntity player) {
        if (!canShoot()) return;
        lastShootTime = Util.time();
        onShoot(player);
    }

    protected abstract void onShoot(PlayerEntity player);
}
