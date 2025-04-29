package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Shapes {
    public static CircleShape circleShape(float radius) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        return circleShape;
    }

    public static PolygonShape boxShape(float x, float y) {
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(x, y);
        return boxShape;
    }
}
