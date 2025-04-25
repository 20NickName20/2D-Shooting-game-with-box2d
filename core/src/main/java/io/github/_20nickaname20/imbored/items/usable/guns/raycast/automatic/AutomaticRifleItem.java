package io.github._20nickaname20.imbored.items.usable.guns.raycast.automatic;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickaname20.imbored.Main;
import io.github._20nickaname20.imbored.items.usable.guns.raycast.AutomaticRaycastGunItem;

public class AutomaticRifleItem extends AutomaticRaycastGunItem {
    public AutomaticRifleItem() {
        super(2, 0.05f, 5, 10, 0.075f, 175f);
    }

    @Override
    public void render(ShapeRenderer renderer, boolean inHand) {
        super.render(renderer, inHand);
        //Main.renderer.setShapeType(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        renderer.rectLine(-0.9f, -0.9f, 0.1f, 0.1f, 1.1f);
        renderer.rectLine(0, 0, 3.5f, 0, 1.1f);
        //Main.renderer.setShapeType(ShapeRenderer.ShapeType.Line);
    }
}
