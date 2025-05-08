package io.github._20nickname20.imbored.game_objects.items.usable.joint.distance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.items.AmmoCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.ammo.PistolCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.joint.DistanceJointItem;

public class HardDistanceJointItem extends DistanceJointItem {
    private static final float SIZE = 0.5f;
    private static final float MAX_DISTANCE = 10f;
    private static final Color COLOR = Color.WHITE;
    private static final float FREQUENCY = 100f;
    private static final float DAMPING = 0.01f;

    public HardDistanceJointItem() {
        super();
    }

    public HardDistanceJointItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return SIZE;
    }
    @Override
    public float getMaxDistance() {
        return MAX_DISTANCE;
    }
    @Override
    public Color getColor() {
        return COLOR;
    }
    @Override
    public float getFrequency() {
        return FREQUENCY;
    }
    @Override
    public float getDamping() {
        return DAMPING;
    }
}
