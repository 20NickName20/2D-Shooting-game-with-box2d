package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.items.scrap.PlankItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.TestGunItem;

import java.util.ArrayList;
import java.util.List;

public class Material {

    public static final Material GROUND = new Material(new Color(0.5f, 0.9f, 0.5f, 1))
        .setFriction(1.5f);
    public static final Material WOOD = new Material(new Color(0.5f, 0.4f, 0.1f, 1))
        .setDensity(0.1f)
        .setFriction(0.75f)
        .setRestitution(0.1f)
        .setLoot(new ScrapLoot(PlankItem.class, 1));
    public static final Material ROCK = new Material(new Color(0.5f, 0.5f, 0.5f, 1))
        .setDensity(0.6f)
        .setFriction(0.9f)
        .setRestitution(0.01f);
    public static final Material METAL = new Material(new Color(0.6f, 0.6f, 0.65f, 1))
        .setDensity(0.5f)
        .setFriction(0.7f)
        .setRestitution(0.05f);
    public static final Material ITEM = new Material(Color.WHITE)
        .setDensity(0.5f)
        .setFriction(0.7f)
        .setRestitution(0.05f);
    public static final Material FLESH = new Material(new Color(0.7f, 0.2f, 0.2f, 1))
        .setDensity(0.05f)
        .setFriction(0.85f)
        .setRestitution(0.01f);
    public static final Material CLOTH = new Material(new Color(0.9f, 0.9f, 0.75f, 1))
        .setDensity(0.05f)
        .setFriction(0.6f)
        .setRestitution(0.1f);

    public float density = 1;
    public float friction = 1;
    public float restitution = 0.001f;
    public final Color color;
    public LootGenerator loot = null;

    private Material(Color color) {
        this.color = color;
    }

    public Material setDensity(float density) {
        this.density = density;
        return this;
    }

    public Material setFriction(float friction) {
        this.friction = friction;
        return this;
    }

    public Material setRestitution(float restitution) {
        this.restitution = restitution;
        return this;
    }

    public Material setLoot(LootGenerator loot) {
        this.loot = loot;
        return this;
    }
}
