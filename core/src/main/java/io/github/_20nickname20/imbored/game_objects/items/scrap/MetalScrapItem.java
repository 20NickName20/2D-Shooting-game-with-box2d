package io.github._20nickname20.imbored.game_objects.items.scrap;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.ScrapItem;

public class MetalScrapItem extends ScrapItem {
    private static final float SIZE = 0.9f;

    public MetalScrapItem() {
        super();
    }

    public MetalScrapItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Material.METAL.color);
        for (float i = -1f; i <= 1f; i += 2f) {
            renderer.rectLine(-1f + i / 2, -1f + i / 4, 1f + i / 2, 1f + i / 4, 0.75f);
        }
    }
}
