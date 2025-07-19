package io.github._20nickname20.imbored.game_objects.entities.container.locked_crate;

import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.container.CrateEntity;
import io.github._20nickname20.imbored.game_objects.entities.container.LockedCrateEntity;

public class LockedWoodenCrateEntity extends LockedCrateEntity {
    public LockedWoodenCrateEntity(GameWorld world, float x, float y, float sizeX, float sizeY) {
        super(world, x, y, sizeX, sizeY, 50f);
    }

    public LockedWoodenCrateEntity(GameWorld world, EntityData data) {
        super(world, data);
    }

    @Override
    public float getMaxHealth() {
        return 150f;
    }

    @Override
    public Material getMaterial() {
        return Material.WOOD;
    }
}
