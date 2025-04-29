package io.github._20nickname20.imbored.items.usable.joint.distance;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.Material;
import io.github._20nickname20.imbored.entities.damagable.living.human.CursorEntity;
import io.github._20nickname20.imbored.items.usable.JointItem;
import io.github._20nickname20.imbored.items.usable.joint.DistanceJointItem;

public class HardDistanceJointItem extends DistanceJointItem {
    public HardDistanceJointItem(Entity holder) {
        super(holder, 0.5f, 10f, Color.WHITE, 100f, 0.01f, 0.999f);
    }
}
