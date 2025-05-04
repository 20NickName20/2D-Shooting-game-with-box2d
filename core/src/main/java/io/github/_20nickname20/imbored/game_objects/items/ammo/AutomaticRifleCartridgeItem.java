package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.util.With;

public class AutomaticRifleCartridgeItem extends AmmoCartridgeItem {
    public AutomaticRifleCartridgeItem(Entity holder) {
        super(holder, 0.3f, 47, 47, new Color(0.3f, 0.5f, 0.3f, 1), new Color(0.6f, 0.5f, 0.0f, 1));
    }
}
