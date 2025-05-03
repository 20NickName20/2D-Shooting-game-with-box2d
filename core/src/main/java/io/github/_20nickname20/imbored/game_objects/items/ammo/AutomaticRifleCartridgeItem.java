package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;

public class AutomaticRifleCartridgeItem extends AmmoCartridgeItem {
    public AutomaticRifleCartridgeItem(Entity holder) {
        super(holder, 0.3f, 47, 47);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Color.LIGHT_GRAY);
        renderer.rect(-0.6f, -1, 0.8f, 2);
        renderer.setColor(Color.YELLOW);
        renderer.rect(-0.65f, 1, 0.9f, 0.1f);
    }
}
