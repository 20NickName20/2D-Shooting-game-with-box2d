package io.github._20nickname20.imbored.game_objects.items;

import com.badlogic.gdx.graphics.Color;
import io.github._20nickname20.imbored.game_objects.Item;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.render.GameRenderer;
import io.github._20nickname20.imbored.util.Util;

public class SixteenPointedAsteriskItem extends Item {
    private static final float SIZE = 0.1f;

    public SixteenPointedAsteriskItem() {
        super();
    }

    public SixteenPointedAsteriskItem(ItemData data) {
        super(data);
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Color.LIGHT_GRAY);
        for (float angle = 0; angle < 180f; angle += 22.5f) {
            renderer.withRotation(angle + Util.time() * 24f, () -> {
                renderer.line(0.75f, 0f, -0.75f, 0f);
            });
        }
    }

    @Override
    public float getSize() {
        return SIZE;
    }
}
