package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.RaycastGunItem;

public abstract class AutomaticRaycastGunItem extends RaycastGunItem {
    public AutomaticRaycastGunItem(Entity holder, float size, float cooldown, float damage, float power, float recoilScale, float range, float maxScatterAngle) {
        super(holder, size, cooldown, damage, power, recoilScale, range, maxScatterAngle, cooldown / 4);
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        this.setUpdating(true);
    }

    @Override
    public void onEndUse(PlayerEntity player) {
        this.setUpdating(false);
    }

    @Override
    public void update(InventoryHolder holder, float dt) {
        shootAttempt((PlayerEntity) holder);
    }
}
