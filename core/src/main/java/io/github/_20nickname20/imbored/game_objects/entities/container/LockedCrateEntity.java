package io.github._20nickname20.imbored.game_objects.entities.container;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.entities.ContainerEntity;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Shapes;

public abstract class LockedCrateEntity extends ContainerEntity {
    protected final float sizeX, sizeY;
    public LockedCrateEntity(GameWorld world, float x, float y, float sizeX, float sizeY, float inventorySize) {
        super(world, x, y, Shapes.boxShape(sizeX, sizeY), inventorySize);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    public LockedCrateEntity(GameWorld world, EntityData data) {
        super(world, data);
        if (data instanceof CrateEntity.CrateEntityData crateEntityData) {
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
        CrateEntity.CrateEntityData crateEntityData;
        if (this.persistentData == null) {
            crateEntityData = new CrateEntity.CrateEntityData();
        } else {
            crateEntityData = (CrateEntity.CrateEntityData) this.persistentData;
        }
        crateEntityData.sizeX = sizeX;
        crateEntityData.sizeY = sizeY;
        this.persistentData = crateEntityData;
        return super.createPersistentData();
    }
}
