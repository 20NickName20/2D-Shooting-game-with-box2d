package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import io.github._20nickname20.imbored.render.PenRenderer;

public class With {
    public static void translation(PenRenderer renderer, float x, float y, Runnable runnable) {
        Matrix4 transformMatrix = renderer.getTransformMatrix().cpy();
        renderer.getTransformMatrix().translate(x, y, 0);
        runnable.run();
        renderer.setTransformMatrix(transformMatrix);
    }

    public static void rotation(PenRenderer renderer, float angle, Runnable runnable) {
        Matrix4 transformMatrix = renderer.getTransformMatrix().cpy();
        renderer.getTransformMatrix().rotate(0, 0, 1f, angle);
        runnable.run();
        renderer.setTransformMatrix(transformMatrix);
    }
}
