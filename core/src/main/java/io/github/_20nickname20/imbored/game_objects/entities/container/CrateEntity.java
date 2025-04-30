package io.github._20nickname20.imbored.game_objects.entities.container;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.util.Shapes;

import static io.github._20nickname20.imbored.util.With.rotation;

public class CrateEntity extends InteractiveContainerEntity {
    private final float sizeX, sizeY;
    public CrateEntity(GameWorld world, float x, float y, float sizeX, float sizeY, float maxHealth) {
        super(world, x, y, Shapes.boxShape(sizeX, sizeY), Material.WOOD, maxHealth, 50);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    private final static float innerPadding = 0.3f;
    @Override
    public boolean render(ShapeRenderer renderer) {
        super.render(renderer);
        float angle = this.b.getAngle() * MathUtils.radiansToDegrees;
        renderer.setColor(this.material.color);
        rotation(renderer, angle, () -> {
            renderer.rect(-sizeX + innerPadding, -sizeY + innerPadding, (sizeX - innerPadding) * 2, (sizeY - innerPadding) * 2);
            renderer.line(-sizeX + 0.5f + innerPadding, -sizeY + innerPadding, sizeX - innerPadding, sizeY - 0.5f - innerPadding);
            renderer.line(-sizeX + innerPadding, -sizeY + 0.5f + innerPadding, sizeX - 0.5f - innerPadding, sizeY - innerPadding);
        });
        return false;
    }
}
