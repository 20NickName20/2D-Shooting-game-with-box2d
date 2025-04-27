package io.github._20nickaname20.imbored.items.usable.guns.raycast;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickaname20.imbored.Main;
import io.github._20nickaname20.imbored.entities.damagable.living.human.CursorEntity;
import io.github._20nickaname20.imbored.entities.damagable.living.human.PlayerEntity;
import io.github._20nickaname20.imbored.items.usable.guns.RaycastGunItem;

public class TestGunItem extends RaycastGunItem {
    public TestGunItem() {
        super(1f, 0.1f, 10f, 50f, 0.04f, 250f, MathUtils.degRad * 2, 0.025f);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        Main.renderer.setShapeType(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        renderer.rectLine(-0.9f, -0.9f, 0.1f, 0.1f, 1.1f);
        renderer.rectLine(0, 0, 2, 0, 1.1f);
        Main.renderer.setShapeType(ShapeRenderer.ShapeType.Line);
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        shootAttempt(player);
    }

    @Override
    public void onEndUse(PlayerEntity player) {

    }
}
