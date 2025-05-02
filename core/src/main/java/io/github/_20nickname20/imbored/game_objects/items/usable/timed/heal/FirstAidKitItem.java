package io.github._20nickname20.imbored.game_objects.items.usable.timed.heal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.HealItem;

public class FirstAidKitItem extends HealItem {
    public FirstAidKitItem(Entity holder) {
        super(holder, 2, 11, 100);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(Color.WHITE);
        renderer.set(ShapeRenderer.ShapeType.Filled);
        renderer.rect(-1.5f, -1.5f, 2f, 2f);
        renderer.setColor(0.7f, 1, 0, 1);
        renderer.rectLine(-1, 0, 1, 0, 0.75f);
        renderer.rectLine(0, -1, 0, 1, 0.75f);
        renderer.set(ShapeRenderer.ShapeType.Line);
    }
}
