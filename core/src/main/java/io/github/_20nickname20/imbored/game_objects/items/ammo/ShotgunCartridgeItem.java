package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.util.With;

public class ShotgunCartridgeItem extends AmmoCartridgeItem {
    public ShotgunCartridgeItem(Entity holder) {
        super(holder, 0.3f, 6, 6, new Color(0.5f, 0.1f, 0.0f, 1), Color.YELLOW);
    }
}
