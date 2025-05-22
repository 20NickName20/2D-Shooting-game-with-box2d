package io.github._20nickname20.imbored.game_objects.items.scrap;

import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.ScrapItem;
import io.github._20nickname20.imbored.render.GameRenderer;

public class StoneItem extends ScrapItem {
    private static final float SIZE = 1.2f;

    public StoneItem() {
        super();
    }

    public StoneItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        renderer.setColor(Material.ROCK.color);
        renderer.polygon(new float[]{
            -0.55f, -0.6f,
            -0.9f, -0.05f,
            -0.6f, 0.5f,
            -0.35f, 1f,

            0.35f, 1f,
            0.6f, 0.5f,
            0.9f, -0.05f,
            0.55f, -0.6f
        });
    }

    /*
        эчпочмак
        renderer.polygon(new float[]{
            -0.7f, -0.6f,
            -0.9f, -0.2f,
            -0.6f, 0.5f,
            -0.2f, 1f,

            0.2f, 1f,
            0.6f, 0.5f,
            0.9f, -0.2f,
            0.7f, -0.6f
        });
     */
}
