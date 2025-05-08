package io.github._20nickname20.imbored.game_objects.items.usable.timed.modirfiers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.effects.SpeedEffect;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.TimedUsableItem;

public class EnergyDrinkItem extends TimedUsableItem {
    private static final float SIZE = 2f;
    private static final float REQUIRED_USE_TIME = 3.5f;

    public EnergyDrinkItem() {
        super();
    }

    public EnergyDrinkItem(ItemData data) {
        super(data);
    }

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public float getRequiredUseTime() {
        return REQUIRED_USE_TIME;
    }

    @Override
    protected void onUseFinish(Entity holder) {
        holder.applyEffect(new SpeedEffect(10, 1));
        super.onUseFinish(holder);
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.5f, 0.5f, 1, 1);
        renderer.rect(-0.5f, -1, 1, 2.2f);
        renderer.setColor(Color.YELLOW);
        renderer.rectLine(-0.5f, 0, 0.5f, 0, 0.4f);
    }
}
