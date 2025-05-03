package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.AutomaticRaycastGunItem;

public class AutomaticRifleItem extends AutomaticRaycastGunItem {
    public AutomaticRifleItem(Entity holder) {
        super(holder, 2, 0.15f, 5, 47, 47, 6.5f, 0.08f, 175f, MathUtils.degRad * 10);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        renderer.rectLine(-0.9f, -0.9f, 0.1f, 0.1f, 1.1f);
        renderer.rectLine(0, 0, 3.5f, 0, 1.1f);
    }
}
