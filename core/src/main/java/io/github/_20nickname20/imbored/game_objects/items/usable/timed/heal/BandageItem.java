package io.github._20nickname20.imbored.game_objects.items.usable.timed.heal;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.HealItem;

public class BandageItem extends HealItem {
    public BandageItem(Entity holder) {
        super(holder, 2, 5, 25);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(Material.CLOTH.color);
        renderer.rect(-0.5f, -1, 1.5f, 1.5f);
    }
}
