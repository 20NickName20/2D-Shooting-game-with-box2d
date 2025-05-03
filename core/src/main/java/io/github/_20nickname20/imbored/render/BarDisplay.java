package io.github._20nickname20.imbored.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.util.Util;

public class BarDisplay {
    private float displayValue;
    private float targetValue;
    private float lastUpdateTime = 0;
    private final float displayTime;
    private final Color outerColor;
    private final Color innerColor;

    public BarDisplay(Color outerColor, Color innerColor, float initValue, float displayTime) {
        this.outerColor = outerColor;
        this.innerColor = innerColor;
        this.displayValue = initValue;
        this.targetValue = initValue;
        this.displayTime = displayTime;
    }

    public void update(float dt) {
        displayValue += (targetValue - displayValue) * dt * 2.5f;
    }

    public void setTargetValue(float targetValue) {
        this.targetValue = targetValue;
        this.lastUpdateTime = Util.time();
    }

    public boolean isRising() {
        return targetValue > displayValue + 0.001f;
    }

    public void render(ShapeRenderer renderer) {
        if (Util.time() - lastUpdateTime > displayTime) return;
        this.forceRender(renderer);
    }

    public void forceRender(ShapeRenderer renderer) {
        renderer.setColor(outerColor);
        renderer.rectLine(-3, 0, 3, 0, 1);
        renderer.setColor(innerColor);
        renderer.rectLine(-2.9f, 0, -2.9f + 5.8f * displayValue, 0, 0.8f);
        renderer.rectLine(-2.9f, 0, -2.9f + 5.8f * displayValue, 0, 0.6f);
    }

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
