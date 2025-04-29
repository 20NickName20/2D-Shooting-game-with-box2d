package io.github._20nickname20.imbored.entities.block;

import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.Material;
import io.github._20nickname20.imbored.entities.BlockEntity;

public class Tnt extends BlockEntity {
    public Tnt(GameWorld world, float x, float y, Shape shape, Material material) {
        super(world, x, y, shape, material, 50);
    }
}
