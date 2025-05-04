package io.github._20nickname20.imbored.game_objects.items.ammo;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;

public class SniperRifleCartridgeItem extends AmmoCartridgeItem {
    public SniperRifleCartridgeItem(Entity holder) {
        super(holder, 0.3f, 1, 6, new Color(0.1f, 0.2f, 0.1f, 1), new Color(Color.GRAY));
    }
}
