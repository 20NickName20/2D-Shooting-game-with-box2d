package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;

public abstract class BaseGunItem extends UsableItem {
    protected float cooldown, damage;
    private int ammo;
    protected final int maxAmmo;
    protected float lastShootTime = 0;
    // TODO: add reloading

    private final BarDisplay ammoBar = new BarDisplay(Color.DARK_GRAY, Color.LIGHT_GRAY, 1, 0.9f);

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
        useAmmo();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        ammoBar.update(dt);
    }

    protected void useAmmo() {
        ammo -= 1;
        ammoBar.setTargetValue((float) ammo / (float) maxAmmo);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        this.renderBar(renderer, handHolder, -5f, ammoBar);
    }
}
