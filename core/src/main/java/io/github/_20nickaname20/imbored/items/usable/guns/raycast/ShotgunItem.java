package io.github._20nickaname20.imbored.items.usable.guns.raycast;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;
import io.github._20nickaname20.imbored.items.usable.guns.RaycastGunItem;

public class ShotgunItem extends RaycastGunItem {
    public ShotgunItem() {
        super(2.5f, 0.7f, 10, 20, 0.15f, 100, MathUtils.degRad * 5,0.05f);
    }

    @Override
    protected void onShoot(PlayerEntity player) {
        for (int i = -2; i <= 2; i++) {
            shootRay(player, (float) (Math.PI / 48f) * i);
        }
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        shootAttempt(player);
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }
}
