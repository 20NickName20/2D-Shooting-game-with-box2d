package io.github._20nickname20.imbored.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.Entity;
import io.github._20nickname20.imbored.Material;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.With;

public abstract class DamagableEntity extends Entity {
    protected float health, maxHealth;
    protected float lastDamageTime = 0;

    public DamagableEntity(World world, float x, float y, Shape shape, Material material, float maxHealth) {
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
    }

    @Override
    public void beginContact(Body other) {
        super.beginContact(other);
        Vector2 relativeVelocity = this.b.getLinearVelocity().cpy().sub(other.getLinearVelocity());
        float len = relativeVelocity.len();
        if (len < 50) return;
        for (JointEdge jointEdge : this.b.getJointList()) {
            if (jointEdge.other == other) return;
        }
        this.damage(len / 7);
    }

    private float displayHealth;
    private final static Color BAR_OUTTER_COLOR = new Color(0.6f, 0.2f, 0, 1);
    private final static Color BAR_INNER_COLOR = new Color(0.5f, 1, 0, 1);
    @Override
    public boolean render(ShapeRenderer renderer) {
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
