package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;

public class PistolCartridgeItem extends AmmoCartridgeItem {
    public PistolCartridgeItem(Entity holder) {
        super(holder, 0.3f, 12, 12);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Color.GRAY);
        renderer.rect(-0.6f, -1, 0.8f, 2);
        renderer.setColor(Color.YELLOW);
        renderer.rect(-0.65f, 1, 0.9f, 0.1f);
    }
}
