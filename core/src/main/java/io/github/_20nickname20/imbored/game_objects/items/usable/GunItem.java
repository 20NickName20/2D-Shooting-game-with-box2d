package io.github._20nickname20.imbored.game_objects.items.usable;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;

public abstract class GunItem extends UsableItem {
    protected float cooldown, damage;
    protected float lastShootTime = 0;
    // TODO: add reloading (and ammo)
    public GunItem(Entity holder, float size, float cooldown, float damage) {
        super(holder, size);
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
