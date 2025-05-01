package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.With;

public abstract class TimedUsableItem extends UsableItem {
    private final float useTime;
    public TimedUsableItem(Entity holder, float size, float useTime) {
        super(holder, size);
        this.useTime = useTime;
    }

    private float usedTime = 0;
    private boolean isUsing = false;

    private final static Color BAR_OUTTER_COLOR = new Color(0.6f, 0.2f, 0, 1);
    private final static Color BAR_INNER_COLOR = new Color(0.95f, 0.9f, 0, 1);

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        if (handHolder == null) return;

        With.rotation(renderer, -handHolder.cursorDirection.angleDeg(), () -> {
            With.translation(renderer, 0, 5f, () -> {
                BarDisplay.render(renderer, BAR_OUTTER_COLOR, BAR_INNER_COLOR, usedTime / useTime);
            });
        });
    }

    protected abstract void onUseFinish(Entity holder);

    @Override
    public void update(float dt) {
        super.update(dt);
        if (isUsing) {
            usedTime += dt;
        }
        if (usedTime >= useTime) {
            onUseFinish(this.getHolder());
            isUsing = false;
        }
    }

    @Override
    public void onStartUse(PlayerEntity player) {
        isUsing = true;
    }

    @Override
    public void onEndUse(PlayerEntity player) {
        isUsing = false;
        usedTime = 0;
    }
}
