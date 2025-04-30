package io.github._20nickname20.imbored.game_objects.entities.block;

import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.render.PenRenderer;
import io.github._20nickname20.imbored.util.Shapes;
import io.github._20nickname20.imbored.game_objects.entities.BlockEntity;

import static io.github._20nickname20.imbored.util.With.rotation;

public class BoxEntity extends BlockEntity {
    private final float sizeX, sizeY;
    public BoxEntity(GameWorld world, float x, float y, int sizeX, int sizeY, Material material, float maxHealth) {
        super(world, x, y, Shapes.boxShape(sizeX, sizeY), material, maxHealth);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    private final static float innerPadding = 0.2f;
    @Override
    public boolean render(PenRenderer renderer) {
        super.render(renderer);
        float angle = this.b.getAngle() * MathUtils.radiansToDegrees;
        renderer.setColor(this.material.color);
        rotation(renderer, angle, () -> {
            // renderer.rect(-sizeX + innerPadding, -sizeY + innerPadding, (sizeX - innerPadding) * 2, (sizeY - innerPadding) * 2);
            renderer.line(-sizeX + 0.5f + innerPadding, -sizeY + innerPadding, sizeX - innerPadding, sizeY - 0.5f - innerPadding);
            renderer.line(-sizeX + innerPadding, -sizeY + 0.5f + innerPadding, sizeX - 0.5f - innerPadding, sizeY - innerPadding);
        });
        return false;
    }
}
