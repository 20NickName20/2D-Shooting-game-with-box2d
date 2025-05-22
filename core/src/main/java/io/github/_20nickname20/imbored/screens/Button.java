package io.github._20nickname20.imbored.screens;

import com.badlogic.gdx.graphics.Color;

abstract class Button {
    public String text;
    private final String initText;
    private final Color color = new Color();
    private final Color initColor = new Color();

    public Button(String text, Color color) {
        this.text = text;
        this.initText = text;
        this.initColor.set(color);
        this.color.set(color);
    }

    public void reset() {
        text = initText;
        setColor(initColor);
    }

    public void setColor(Color color) {
        this.color.set(color);
    }

    public Color getColor() {
        return color;
    }

    public void addColor(float r, float g, float b) {
        this.color.add(r, g, b, 0);
    }

    public abstract void onUse();
}
