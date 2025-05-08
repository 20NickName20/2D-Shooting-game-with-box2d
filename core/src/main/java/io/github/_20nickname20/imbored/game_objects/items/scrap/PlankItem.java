package io.github._20nickname20.imbored.game_objects.items.scrap;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.ScrapItem;

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
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Material.WOOD.color);
        renderer.rectLine(-1.5f, -1.5f, 1.5f, 1.5f, 1);
    }
}
