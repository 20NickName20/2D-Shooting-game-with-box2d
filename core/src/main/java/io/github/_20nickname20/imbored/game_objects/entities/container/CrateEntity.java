package io.github._20nickname20.imbored.game_objects.entities.container;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Shapes;

public abstract class CrateEntity extends InteractableContainerEntity {
    private final float sizeX, sizeY;
    public CrateEntity(GameWorld world, float x, float y, float sizeX, float sizeY) {
        super(world, x, y, Shapes.boxShape(sizeX, sizeY), 50);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public CrateEntity(GameWorld world, EntityData data) {
        super(world, data);
        if (data instanceof CrateEntityData crateEntityData) {
            sizeX = crateEntityData.sizeX;
            sizeY = crateEntityData.sizeY;
        } else {
            sizeX = 0;
            sizeY = 0;
        }
    }

    private final static float innerPadding = 0.3f;
    @Override
    public boolean render(GameRenderer renderer) {
        super.render(renderer);
        float angle = this.b.getAngle() * MathUtils.radiansToDegrees;
        renderer.setColor(getMaterial().color);
        renderer.withRotation(angle, () -> {
            renderer.rect(-sizeX + innerPadding, -sizeY + innerPadding, (sizeX - innerPadding) * 2, (sizeY - innerPadding) * 2);
            renderer.line(-sizeX + 0.5f + innerPadding, -sizeY + innerPadding, sizeX - innerPadding, sizeY - 0.5f - innerPadding);
            renderer.line(-sizeX + innerPadding, -sizeY + 0.5f + innerPadding, sizeX - 0.5f - innerPadding, sizeY - innerPadding);
        });
        return false;
    }

    @Override
    public EntityData createPersistentData() {
        CrateEntityData crateEntityData;
        if (this.persistentData == null) {
            crateEntityData = new CrateEntityData();
        } else {
            crateEntityData = (CrateEntityData) this.persistentData;
        }
        crateEntityData.sizeX = sizeX;
        crateEntityData.sizeY = sizeY;
        this.persistentData = crateEntityData;
        return super.createPersistentData();
    }

    public static class CrateEntityData extends ContainerEntityData {
        float sizeX, sizeY;
    }
}
