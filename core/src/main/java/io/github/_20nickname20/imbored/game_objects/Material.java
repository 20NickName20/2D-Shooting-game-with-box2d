package io.github._20nickname20.imbored.game_objects;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.items.scrap.MetalScrapItem;
import io.github._20nickname20.imbored.game_objects.items.scrap.PlankItem;
import io.github._20nickname20.imbored.game_objects.items.scrap.StoneItem;
import io.github._20nickname20.imbored.game_objects.loot.ScrapLoot;

import java.util.HashMap;
import java.util.Map;

public class Material {

    private static final Map<String, Material> materialMap = new HashMap<>();


    public static final Material GROUND = new Material("ground", new Color(0.5f, 0.9f, 0.5f, 1))
        .setFriction(1.5f);
    public static final Material WOOD = new Material("wood", new Color(0.5f, 0.4f, 0.1f, 1))
        .setDensity(0.1f)
        .setFriction(0.75f)
        .setRestitution(0.1f)
        .setHealthPerUnit(4.5f)
        .setLoot(new ScrapLoot(PlankItem.class, 0.15f));
    public static final Material ROCK = new Material("rock", new Color(0.5f, 0.5f, 0.5f, 1))
        .setDensity(0.6f)
        .setFriction(0.9f)
        .setRestitution(0.01f)
        .setHealthPerUnit(12.5f)
        .setLoot(new ScrapLoot(StoneItem.class, 0.4f));
    public static final Material METAL = new Material("metal", new Color(0.6f, 0.6f, 0.65f, 1))
        .setDensity(0.5f)
        .setFriction(0.7f)
        .setRestitution(0.05f)
        .setHealthPerUnit(7.5f)
        .setLoot(new ScrapLoot(MetalScrapItem.class, 0.075f));
    public static final Material ITEM = new Material("item", Color.WHITE)
        .setDensity(0.3f)
        .setFriction(0.7f)
        .setRestitution(0.05f)
        .setHealthPerUnit(500f);
    public static final Material FLESH = new Material("flesh", new Color(0.7f, 0.2f, 0.2f, 1))
        .setDensity(0.1f)
        .setFriction(0.85f)
        .setRestitution(0.01f);
    public static final Material CLOTH = new Material("cloth", new Color(0.9f, 0.9f, 0.75f, 1))
        .setDensity(0.05f)
        .setFriction(0.6f)
        .setRestitution(0.1f);

    public float density = 1;
    public float friction = 1;
    public float restitution = 0.001f;
    public float healthPerUnit = 1;
    public final Color color;
    public LootGenerator loot = null;

    public final String id;

    private Material(String id, Color color) {
        this.id = id;
        this.color = color;

        materialMap.put(id, this);
    }

    public static Material valueOf(String id) {
        return materialMap.get(id);
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

    public Material setHealthPerUnit(float healthPerUnit) {
        this.healthPerUnit = healthPerUnit;
        return this;
    }

    public Material setLoot(LootGenerator loot) {
        this.loot = loot;
        return this;
    }
}
