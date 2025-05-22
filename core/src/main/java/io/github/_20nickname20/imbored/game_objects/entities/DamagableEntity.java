package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.With;

import java.util.ArrayList;
import java.util.List;

public abstract class DamagableEntity extends Entity {
    protected float health;
    private float damageScale = 1.0f;
    protected float lastHealthChange = 0;
    private final BarDisplay healthBar = new BarDisplay(new Color(0.6f, 0.2f, 0, 1), new Color(0.5f, 1, 0, 1), 1, 1);

    public DamagableEntity(GameWorld world, float x, float y, Shape shape) {
        super(world, x, y, shape);
    }

    public DamagableEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public void onSpawn(World world) {
        super.onSpawn(world);
        if (this.persistentData instanceof DamagableEntityData damagableEntityData) {
            this.health = damagableEntityData.health;
        } else {
            this.health = getMaxHealth();
        }
    }

    public abstract float getMaxHealth();

    public float getDamageScale() {
        return damageScale;
    }

    public void setDamageScale(float damageScale) {
        this.damageScale = damageScale;
    }

    public void scaleDamage(float scale) {
        this.damageScale *= scale;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void heal(float amount) {
        if (isRemoved()) return;
        this.health += amount;
        lastHealthChange = Util.time();
        if (this.health > getMaxHealth()) {
            this.health = getMaxHealth();
        }
        healthBar.setTargetValue(health / getMaxHealth());
    }

    public void damage(float amount) {
        if (isRemoved()) return;
        amount *= damageScale;
        this.health -= amount;
        lastHealthChange = Util.time();
        if (this.health <= 0) {
            onDestroy();
        }
        healthBar.setTargetValue(health / getMaxHealth());
    }

    public void kill() {
        this.damage(Float.MAX_VALUE);
    }

    public void onDestroy() {
        this.remove();
        List<Item> droppedItems = getDroppedItems();
        Vector2 position = this.b.getPosition();
        for (Item item : droppedItems) {
            gameWorld.dropItem(position, new Vector2(MathUtils.random(-1, 1), MathUtils.random(-1, 1)).scl(27f), item);
        }
    }

    public List<Item> getDroppedItems() {
        List<Item> droppedItems = new ArrayList<>();
        if (getMaterial().loot != null) {
            droppedItems.addAll(getMaterial().loot.generate(this.area));
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

    @Override
    public void update(float dt) {
        super.update(dt);
        healthBar.update(dt);
    }

    @Override
    public boolean render(GameRenderer renderer) {
        super.render(renderer);
        renderer.withTranslation(0, 5, () -> {
            healthBar.render(renderer);
        });
        return false;
    }

    @Override
    public EntityData createPersistentData() {
        DamagableEntityData damagableEntityData;
        if (this.persistentData == null) {
            damagableEntityData = new DamagableEntityData();
        } else {
            damagableEntityData = (DamagableEntityData) this.persistentData;
        }
        damagableEntityData.health = this.health;
        this.persistentData = damagableEntityData;
        return super.createPersistentData();
    }

    public static class DamagableEntityData extends EntityData {
        public float health;
    }
}
