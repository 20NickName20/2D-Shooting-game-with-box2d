package io.github._20nickaname20.imbored.items.usable;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickaname20.imbored.Util;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;
import io.github._20nickaname20.imbored.items.UsableItem;

import java.sql.Time;

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

    public boolean shootFrom(PlayerEntity player) {
        if (!canShoot()) return false;
        lastShootTime = Util.time();
        return true;
    }
}
