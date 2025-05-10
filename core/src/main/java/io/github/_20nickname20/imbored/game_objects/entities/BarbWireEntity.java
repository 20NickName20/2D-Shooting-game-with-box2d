package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.util.Shapes;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.HashMap;

public class BarbWireEntity extends DamagableEntity{
    public final float MAXHEALTH = 30f;
    public final float TIMEPERIOD = 1f;
    public final Vector2 POSPERIOD = new Vector2(1, 0);
    public final float DAMAGEPERPOS = 30f;
    public final float LENGTH = 5f;

    public BarbWireEntity(GameWorld world, float x, float y) {
        super(world, x, y, Shapes.boxShape(5, 1));
    }

    public BarbWireEntity(GameWorld world, EntityData data){super(world, data);}

    @Override
    public float getMaxHealth() {
        return MAXHEALTH;
    }

    @Override
    public Material getMaterial() {
        return Material.METAL;
    }

    private final HashMap<Entity, Float> touchTimes = new HashMap<>();
    private final HashMap<Entity, Vector2> lastPositon = new HashMap<>();


    @Override
    public void beginContact(Body other){
        super.beginContact(other);

        if (!(other.getUserData() instanceof DamagableEntity otherentity)) return;
        float timePassed = Util.time() - touchTimes.getOrDefault(otherentity, Util.time());
        Vector2 positionChange = otherentity.b.getPosition().add(lastPositon.getOrDefault(otherentity, otherentity.b.getPosition())) ;
        touchTimes.put(otherentity, timePassed);
        lastPositon.put(otherentity, positionChange);
        if (positionChange.dot(POSPERIOD) > 0.5f) {
            if (timePassed > TIMEPERIOD) {
                otherentity.damage(DAMAGEPERPOS);
            }
        }
    }

    @Override
    public boolean render(ShapeRenderer renderer){
        super.render(renderer);
        renderer.setColor(Color.GRAY);
        With.rotation(renderer, this.b.getAngle() * MathUtils.radiansToDegrees, () ->{
            for (float i = 0; i < LENGTH; i++) {
                renderer.circle(i + 2f, 1f, 2f);
            }
        });
        return true;
    }
}
