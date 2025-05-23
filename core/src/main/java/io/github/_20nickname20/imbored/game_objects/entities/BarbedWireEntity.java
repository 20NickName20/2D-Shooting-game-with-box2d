package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Shapes;

import java.util.Random;

public class BarbedWireEntity extends DamagableEntity{
    public final float MAX_HEALTH = 30f;

    public BarbedWireEntity(GameWorld world, float x, float y) {
        super(world, x, y, Shapes.boxShape(5, 1));
    }

    public BarbedWireEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public void onSpawn(World world) {
        super.onSpawn(world);

        FixtureDef fixtureDef = new FixtureDef();
        Shape sensorShape = Shapes.boxShape(4.8f, 0.8f);
        fixtureDef.shape = sensorShape;
        fixtureDef.isSensor = true;
        this.b.createFixture(fixtureDef);
        sensorShape.dispose();

        this.seed = random.nextLong();
    }

    @Override
    public float getMaxHealth() {
        return MAX_HEALTH;
    }

    @Override
    public Material getMaterial() {
        return Material.LIGHT_METAL;
    }

    private final Random random = new Random();
    private long seed = 0;

    @Override
    public boolean render(GameRenderer renderer){
        random.setSeed(seed);
        super.render(renderer);
        renderer.setColor(Color.GRAY);
        renderer.withRotation(this.b.getAngle() * MathUtils.radiansToDegrees, () ->{
            for (float i = 0; i < 9; i++) {
                renderer.withTranslation(i - 4f, random.nextFloat() / 2f - 0.25f, () -> {
                    renderer.withRotation(random.nextFloat(0f, 90f), () -> {
                        renderer.circle(0, 0, 1.2f, random.nextInt(3) + 4);
                    });
                });
            }
        });
        return true;
    }

    @Override
    public boolean shouldCollide(Entity other) {
        if (other instanceof LivingEntity) {
            return false;
        }
        return super.shouldCollide(other);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        for (Body body : this.contacts) {
            if (body.getUserData() instanceof DamagableEntity entity) {
                if (entity instanceof LivingEntity) {
                    entity.damage(dt / 3f);
                }

                entity.damage((float) (Math.pow(body.getLinearVelocity().cpy().sub(this.b.getLinearVelocity()).len(), 1.45f) / 2f * dt));
            }
        }
    }
}
