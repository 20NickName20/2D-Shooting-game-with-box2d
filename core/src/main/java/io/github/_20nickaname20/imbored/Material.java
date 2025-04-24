package io.github._20nickaname20.imbored;

import com.badlogic.gdx.graphics.Color;

public enum Material {
    CARBON(0.0001f, 1f, 0.5f, 1f, new Color(0.1f, 0, 0, 1)),
    WOOD(0.1f, 0.75f, 0.1f, 1, new Color(0.5f, 0.4f, 0.1f, 1)),
    ROCK(0.6f, 0.9f, 0.01f, 1, new Color(0.5f, 0.5f, 0.5f, 1)),
    METAL(0.5f, 0.7f, 0.05f, 1, new Color(0.6f, 0.6f, 0.65f, 1)),
    FLESH(0.05f, 0.9f, 0.01f, 1, new Color(0.7f, 0.2f, 0.2f, 1));

    Material(float density, float friction, float restitution, float durability, Color color) {
        this.density = density;
        this.friction = friction;
        this.restitution = restitution;
        this.durability = durability;
        this.color = color;
    }

    public final float density, friction, restitution, durability;
    public final Color color;
}
