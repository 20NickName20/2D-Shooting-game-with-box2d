package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.ammo.PistolCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.SniperRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.RaycastGunItem;

public class SniperGunItem extends RaycastGunItem {
    public SniperGunItem(Entity holder) {
        super(holder, 2f, 0.7f, 15.5f,1,1, 11f,34f, 0.09f, 370f, MathUtils.degRad * 5, 1.275f, SniperRifleCartridgeItem.class);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.2f, 0.3f, 0.0f, 1);
        renderer.rectLine(-0.75f, -0.75f, 0.1f, 0.1f, 0.8f);
        renderer.rectLine(0, 0, 6.5f, 0, 0.9f);
        renderer.rectLine(3.0f, 0.7f, 0.3f, 0.7f, 0.5f);
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        shootAttempt(player);
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }
}
