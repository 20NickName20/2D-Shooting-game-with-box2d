package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.render.PenRenderer;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.With;

import java.util.ArrayList;
import java.util.List;

public abstract class DamagableEntity extends Entity {
    protected float health, maxHealth;
    protected float lastDamageTime = 0;

    public DamagableEntity(GameWorld world, float x, float y, Shape shape, Material material, float maxHealth) {
        super(world, x, y, shape, material);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.displayHealth = maxHealth;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void heal(float amount) {
        this.health += amount;
        if (this.health > this.maxHealth) {
            this.health = maxHealth;
        }
    }

    public void damage(float amount) {
        if (isRemoved()) return;
        this.health -= amount;
        lastDamageTime = Util.time();
        if (this.health <= 0) {
            onDestroy();
        }
    }

    public void onDestroy() {
        this.remove();
        List<Item> droppedItems = getDroppedItems();
        Vector2 position = this.b.getPosition();
        for (Item item : droppedItems) {
            gameWorld.dropItem(position, new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).scl(20), item);
        }
    }

    public List<Item> getDroppedItems() {
        List<Item> droppedItems = new ArrayList<>();
        if (material.loot != null) {
            droppedItems.addAll(material.loot.generate(this.b.getMass()));
        }
        return droppedItems;
    }

    @Override
    public void beginContact(Body other) {
        super.beginContact(other);
        Vector2 relativeVelocity = this.b.getLinearVelocity().cpy().sub(other.getLinearVelocity());
        float len = relativeVelocity.len();
        if (len < 100) return;
        relativeVelocity.setLength(len - 100);
        for (JointEdge jointEdge : this.b.getJointList()) {
            if (jointEdge.other == other) return;
        }
        this.damage(len / 10);
    }

    private float displayHealth;
    private final static Color BAR_OUTTER_COLOR = new Color(0.6f, 0.2f, 0, 1);
    private final static Color BAR_INNER_COLOR = new Color(0.5f, 1, 0, 1);
    @Override
    public boolean render(PenRenderer renderer) {
        float time = Util.time();
        float dt = time - lastDamageTime;
        displayHealth += (health - displayHealth) * dt;
        if (time - lastDamageTime > 1) return false;
        With.translation(renderer, 0, 5, () -> {
            BarDisplay.render(renderer, BAR_OUTTER_COLOR, BAR_INNER_COLOR, displayHealth / maxHealth);
        });
        return false;
    }
}
