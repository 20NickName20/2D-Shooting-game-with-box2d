package io.github._20nickname20.imbored.game_objects.entities.block;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;

public final class SimpleBlockEntity extends BlockEntity {
    private Material material = Material.GROUND;

    public SimpleBlockEntity(GameWorld world, float x, float y, Shape shape, Material material) {
        super(world, x, y, shape);
        this.material = material;
    }

    public SimpleBlockEntity(GameWorld world, EntityData data) {
        super(world, data);
        if (data instanceof MaterialData materialData) {
            this.material = Material.valueOf(materialData.material);
        } else {
            this.material = Material.WOOD;
        }
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public EntityData createPersistentData() {
        MaterialData materialData;
        if (this.persistentData == null) {
            materialData = new MaterialData();
        } else {
            materialData = (MaterialData) this.persistentData;
        }
        materialData.material = material.id;
        this.persistentData = materialData;
        return super.createPersistentData();
    }

    public final static class MaterialData extends DamagableEntityData {
        String material;
    }
}
