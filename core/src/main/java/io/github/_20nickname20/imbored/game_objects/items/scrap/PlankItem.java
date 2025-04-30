package io.github._20nickname20.imbored.game_objects.items.scrap;

import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.ScrapItem;
import io.github._20nickname20.imbored.render.PenRenderer;

public class PlankItem extends ScrapItem {
    public PlankItem(Entity holder) {
        super(holder, 0.4f);
    }

    @Override
    public void render(PenRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Material.WOOD.color);
        // renderer.rectLine(-1.5f, -1.5f, 1.5f, 1.5f, 1);
    }
}
