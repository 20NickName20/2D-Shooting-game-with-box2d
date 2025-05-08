package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.util.Shapes;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.HashMap;

public class LandmineEntity extends DamagableEntity {
    private final static float POWER = 15;
    private final static float DAMAGE = 35;
    private final static float RANGE = 75;
    public LandmineEntity(GameWorld gameWorld, float x, float y) {
        super(gameWorld, x, y, Shapes.boxShape(3, 1));
    }

    public LandmineEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public Material getMaterial() {
        return Material.METAL;
    }

    @Override
    public float getMaxHealth() {
        return 30f;
    }

    private final HashMap<Entity, Float> touchTimes = new HashMap<>();

    @Override
    public void endContact(Body other) {
        super.endContact(other);

        if (!(other.getUserData() instanceof Entity otherEntity)) return;
        float timePassed = Util.time() - touchTimes.getOrDefault(otherEntity, Util.time());
        touchTimes.put(otherEntity, timePassed);
        if (other.getPosition().y > this.b.getPosition().y) {
            if (timePassed > 0.06f) {
                Util.explode(gameWorld, this.b, this.b.getPosition(), (float) (Math.PI / 12), RANGE, POWER, DAMAGE, 50, 25, Color.ORANGE);
                this.remove();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.explode(gameWorld, this.b, this.b.getPosition(), (float) (Math.PI / 12), RANGE, POWER, DAMAGE, 50, 25, Color.ORANGE);
    }

    @Override
    public boolean render(ShapeRenderer renderer) {
        super.render(renderer);
        renderer.setColor(0, 0.5f, 0.1f, 1);
        With.rotation(renderer, this.b.getAngle() * MathUtils.radiansToDegrees, () -> {
            renderer.rect(-3, -1, 6, 2);
        });
        return true;
    }
}
