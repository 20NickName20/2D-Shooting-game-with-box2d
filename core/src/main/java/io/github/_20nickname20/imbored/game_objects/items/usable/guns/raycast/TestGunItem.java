package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.RaycastGunItem;
import io.github._20nickname20.imbored.render.PenRenderer;

public class TestGunItem extends RaycastGunItem {
    public TestGunItem(Entity holder) {
        super(holder, 1f, 0.1f, 10f, 50f, 0.04f, 250f, MathUtils.degRad * 2, 0.025f);
    }

    @Override
    public void render(PenRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        // renderer.rectLine(-0.9f, -0.9f, 0.1f, 0.1f, 1.1f);
        /*
        renderer.polygon(new float[]{
            0f, 0.5f,
            2f, 0.9f,
            2f, -0.9f,
            0f, -0.5f,
        });

         */
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        shootAttempt(player);
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }
}
