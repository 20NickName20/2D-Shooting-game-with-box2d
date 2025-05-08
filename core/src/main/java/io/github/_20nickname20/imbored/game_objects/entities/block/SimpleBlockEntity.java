package io.github._20nickname20.imbored.game_objects.entities.block;

import com.badlogic.gdx.physics.box2d.Shape;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;

public class SimpleBlockEntity extends BlockEntity {
    private final Material material;

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
            try {
                materialData = (MaterialData) this.persistentData;
            } catch (ClassCastException e) {
                materialData = new MaterialData();
            }
        }
        materialData.material = material.id;
        return super.createPersistentData();
    }

    public static class MaterialData extends EntityData {
        String material;
    }
}
