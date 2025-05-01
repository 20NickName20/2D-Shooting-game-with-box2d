package io.github._20nickname20.imbored.game_objects.items.usable.joint.distance;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.DistanceJointItem;

public class RubberDistanceJointItem extends DistanceJointItem {
    public RubberDistanceJointItem(Entity holder) {
        super(holder, 0.5f, 20f, Color.CYAN, 5f, 0.5f, 0.75f);
    }
}
