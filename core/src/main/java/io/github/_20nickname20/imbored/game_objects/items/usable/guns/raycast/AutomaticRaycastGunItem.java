package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.RaycastGunItem;

public abstract class AutomaticRaycastGunItem extends RaycastGunItem {
    public AutomaticRaycastGunItem(int ammo) {
        super(ammo);
    }

    public AutomaticRaycastGunItem(ItemData data) {
        super(data);
    }

    private boolean isShooting = false;

    public boolean isShooting() {
        return isShooting;
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        if (reloadingAmmo != null) return;
        this.isShooting = true;
    }

    @Override
    public void onEndUse(PlayerEntity player) {
        this.isShooting = false;
    }

    @Override
    public void onOtherAppliedStart(Item other) {
        if (isShooting) return;
        super.onOtherAppliedStart(other);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (this.isShooting) {
            if (this.getHolder() instanceof PlayerEntity player) {
                shootAttempt(player);
            }
        }
    }
}
