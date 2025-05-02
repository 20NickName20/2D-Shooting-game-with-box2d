package io.github._20nickname20.imbored.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BarDisplay {
    public static void render(ShapeRenderer renderer, Color outter, Color inner, float value) {
        renderer.setColor(outter);
        renderer.rectLine(-3, 0, 3, 0, 1);
        renderer.setColor(inner);
        renderer.rectLine(-2.9f, 0, -2.9f + 5.8f * value, 0, 0.8f);
        renderer.rectLine(-2.9f, 0, -2.9f + 5.8f * value, 0, 0.6f);
    }

    public static void render(ShapeRenderer renderer, Color outter, Color inner, float value, float width) {
        renderer.setColor(outter);
        renderer.rectLine(-3 - width / 2, 0, 3, 0, width);
        renderer.setColor(inner);
        renderer.rectLine(-2.9f - width / 2, 0, -2.9f + 5.8f * value * width, 0, 0.8f);
        renderer.rectLine(-2.9f - width / 2, 0, -2.9f + 5.8f * value * width, 0, 0.6f);
    }
}
