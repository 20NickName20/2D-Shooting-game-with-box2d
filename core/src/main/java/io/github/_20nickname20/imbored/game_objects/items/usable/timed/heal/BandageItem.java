package io.github._20nickname20.imbored.game_objects.items.usable.timed.heal;

import io.github._20nickname20.imbored.game_objects.Material;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.timed.HealItem;
import io.github._20nickname20.imbored.render.GameRenderer;

public class BandageItem extends HealItem {
    private static final float SIZE = 2f;
    private static final float REQUIRED_USE_TIME = 5f;
    private static final float HEAL_AMOUNT = 25f;

    public BandageItem() {
        super();
    }

    public BandageItem(ItemData data) {
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
        renderer.setColor(Material.CLOTH.color);
        renderer.rect(-0.5f, -1, 1.5f, 1.5f);
    }
}
