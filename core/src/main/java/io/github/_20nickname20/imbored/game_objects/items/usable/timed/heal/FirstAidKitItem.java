package io.github._20nickname20.imbored.game_objects.items.usable.timed.heal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.HealItem;
import io.github._20nickname20.imbored.render.GameRenderer;

public class FirstAidKitItem extends HealItem {
    private static final float SIZE = 4f;
    private static final float REQUIRED_USE_TIME = 10f;
    private static final float HEAL_AMOUNT = 75f;

    public FirstAidKitItem() {
        super();
    }

    public FirstAidKitItem(ItemData data) {
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
    public float getHealAmount() {
        return HEAL_AMOUNT;
    }

    @Override
    public void render(GameRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(Color.WHITE);
        renderer.rect(-1.5f, -1.5f, 3f, 3f);
        renderer.setColor(0.7f, 1, 0, 1);
        renderer.rect(-1f, -0.5f, 2f, 1f);
        renderer.rect(-0.5f, -1f, 1f, 2f);
    }
}
