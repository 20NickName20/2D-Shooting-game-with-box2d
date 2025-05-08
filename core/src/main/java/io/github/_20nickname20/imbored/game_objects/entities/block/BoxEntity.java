package io.github._20nickname20.imbored.game_objects.entities.block;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.util.Shapes;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;
import io.github._20nickname20.imbored.util.Util;

import static io.github._20nickname20.imbored.util.With.rotation;

public abstract class BoxEntity extends BlockEntity {
    private final float sizeX, sizeY;

    public BoxEntity(GameWorld world, float x, float y, int sizeX, int sizeY) {
        super(world, x, y, Shapes.boxShape(sizeX, sizeY));
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public BoxEntity(GameWorld world, EntityData data) {
        super(world, data);
        if (data instanceof BoxEntityData boxEntityData) {
            this.sizeX = boxEntityData.sizeX;
            this.sizeY = boxEntityData.sizeY;
        } else {
            this.sizeX = 0;
            this.sizeY = 0;
        }
    }

    @Override
    public EntityData createPersistentData() {
        BoxEntityData boxEntityData;
        if (this.persistentData == null) {
            boxEntityData = new BoxEntityData();
        } else {
            boxEntityData = (BoxEntityData) this.persistentData;
        }
        boxEntityData.sizeX = sizeX;
        boxEntityData.sizeY = sizeY;
        this.persistentData = boxEntityData;
        return super.createPersistentData();
    }

    private final static float innerPadding = 0.2f;
    @Override
    public boolean render(ShapeRenderer renderer) {
        super.render(renderer);
        float angle = this.b.getAngle() * MathUtils.radiansToDegrees;
        renderer.setColor(getMaterial().color);
        rotation(renderer, angle, () -> {
            renderer.rect(-sizeX + innerPadding, -sizeY + innerPadding, (sizeX - innerPadding) * 2, (sizeY - innerPadding) * 2);
            renderer.line(-sizeX + 0.5f + innerPadding, -sizeY + innerPadding, sizeX - innerPadding, sizeY - 0.5f - innerPadding);
            renderer.line(-sizeX + innerPadding, -sizeY + 0.5f + innerPadding, sizeX - 0.5f - innerPadding, sizeY - innerPadding);
        });
        return false;
    }

    public static class BoxEntityData extends DamagableEntityData {
        public float sizeX;
        public float sizeY;
    }
}
