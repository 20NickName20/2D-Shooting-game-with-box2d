package io.github._20nickaname20.imbored.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickaname20.imbored.Entity;
import io.github._20nickaname20.imbored.Material;
import io.github._20nickaname20.imbored.Util;

public abstract class DamagableEntity extends Entity {
    protected float health, maxHealth;
    protected float lastDamageTime = 0;

    public DamagableEntity(World world, float x, float y, Shape shape, Material material, float maxHealth) {
        super(world, x, y, shape, material);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void damage(float amount) {
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
    public boolean render(ShapeRenderer renderer) {
        if (Util.time() - lastDamageTime > 1) return false;
        renderer.setColor(0.6f, 0.2f, 0, 1);
        renderer.rectLine(-3, 5, 3, 5, 1);
        renderer.setColor(0.5f, 1, 0, 1);
        renderer.rectLine(-2.9f, 5, -2.9f + 5.8f * health / maxHealth, 5, 0.8f);
        return false;
    }
}
