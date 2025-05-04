package io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.effects.ResistanceEffect;
import io.github._20nickname20.imbored.game_objects.effects.SpeedEffect;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.TimedUsableItem;

public class ShieldPotionItem extends TimedUsableItem {
    public ShieldPotionItem(Entity holder) {
        super(holder, 1, 4);
    }

    @Override
    protected void onUseFinish(Entity holder) {
        holder.applyEffect(new ResistanceEffect(10, 1));
        super.onUseFinish(holder);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.0f, 0.5f, 0.9f, 1);
        renderer.ellipse(-0.7f, -1, 1.1f, 2.3f);
        renderer.setColor(Color.OLIVE);
        renderer.rectLine(-0.4f, 0, 0.5f, 0, 0.3f);
    }
}
