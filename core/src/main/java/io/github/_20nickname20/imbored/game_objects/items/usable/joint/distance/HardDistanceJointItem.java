package io.github._20nickname20.imbored.game_objects.items.usable.joint.distance;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.DistanceJointItem;

public class HardDistanceJointItem extends DistanceJointItem {
    public HardDistanceJointItem(Entity holder) {
        super(holder, 0.5f, 10f, Color.WHITE, 100f, 0.01f);
    }
}
