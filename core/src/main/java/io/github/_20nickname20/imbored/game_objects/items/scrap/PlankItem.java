package io.github._20nickname20.imbored.game_objects.items.scrap;

import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.ScrapItem;
import io.github._20nickname20.imbored.render.GameRenderer;

public class PlankItem extends ScrapItem {
    private static final float SIZE = 0.5f;

    public PlankItem() {
        super();
    }

    public PlankItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Material.WOOD.color);
        renderer.withRotation(45f, () -> {
            renderer.rect(-2f, -0.5f, 4f, 1f);
        });
    }
}
