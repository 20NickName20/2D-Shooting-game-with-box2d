package io.github._20nickname20.imbored.game_objects.items.scrap;

import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.ScrapItem;
import io.github._20nickname20.imbored.render.GameRenderer;

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
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Material.METAL.color);
        for (float i = -1f; i <= 1f; i += 2f) {
            renderer.rect(-0.75f + i / 2, -0.5f + i / 4, 2f, 0.9f);
        }
    }
}
