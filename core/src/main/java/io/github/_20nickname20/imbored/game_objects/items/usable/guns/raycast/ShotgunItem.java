package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.RaycastGunItem;

public class ShotgunItem extends RaycastGunItem {
    public ShotgunItem(Entity holder) {
        super(holder, 2.5f, 0.7f, 7.5f, 20, 0.15f, 100, MathUtils.degRad * 5,0.05f);
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

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);

        renderer.setColor(Material.WOOD.color);
        renderer.rectLine(-0.9f, -0.9f, 0.1f, 0.1f, 1.1f);
        renderer.rectLine(0, 0, 3, 0, 1.1f);
    }
}
