package io.github._20nickname20.imbored.game_objects.entities;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Shapes;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.util.With;

import java.util.ArrayList;
import java.util.List;

public class LandmineEntity extends DamagableEntity {
    public LandmineEntity(GameWorld gameWorld, float x, float y) {
        super(gameWorld, x, y, Shapes.boxShape(3, 1), Material.METAL, 30);
    }

    @Override
    public void endContact(Body other) {
        super.endContact(other);
        super.beginContact(other);
        if (other.getPosition().y > this.b.getPosition().y) {
            Util.explode(gameWorld, this.b, new ArrayList<>(), this.b.getPosition(), (float) (Math.PI / 12), 60, 20, 10);
            this.remove();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util.explode(gameWorld, this.b, new ArrayList<>(), this.b.getPosition(), (float) (Math.PI / 12), 60, 20, 10);
    }

    @Override
    public boolean render(ShapeRenderer renderer) {
        renderer.setColor(0, 0.5f, 0.1f, 1);
        With.rotation(renderer, this.b.getAngle() * MathUtils.radiansToDegrees, () -> {
            renderer.rect(-3, -1, 6, 2);
        });
        return true;
    }
}
