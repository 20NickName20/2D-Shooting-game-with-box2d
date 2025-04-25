package io.github._20nickaname20.imbored.entities.block;

import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import io.github._20nickaname20.imbored.Material;
import io.github._20nickaname20.imbored.Util;
import io.github._20nickaname20.imbored.entities.BlockEntity;

public class BoxEntity extends BlockEntity {
    private float sizeX, sizeY;
    public BoxEntity(World world, float x, float y, int sizeX, int sizeY, Material material, float maxHealth) {
        super(world, x, y, Util.boxShape(sizeX, sizeY), material, maxHealth);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    private final static float innerPadding = 0.2f;
    @Override
    public boolean render(ShapeRenderer renderer) {
        super.render(renderer);
        float angle = this.b.getAngle() * MathUtils.radiansToDegrees;
        renderer.setColor(getMaterial().color);
        renderer.rotate(0, 0, 1, angle);
        renderer.rect(-sizeX + innerPadding, -sizeY + innerPadding, (sizeX - innerPadding) * 2, (sizeY - innerPadding) * 2);
        renderer.line(-sizeX + 0.5f + innerPadding, -sizeY + innerPadding, sizeX - innerPadding, sizeY - 0.5f - innerPadding);
        renderer.line(-sizeX + innerPadding, -sizeY + 0.5f + innerPadding, sizeX - 0.5f - innerPadding, sizeY - innerPadding);
        renderer.rotate(0, 0, 1, -angle);
        return false;
    }
}
