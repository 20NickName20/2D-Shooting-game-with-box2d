package io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.automatic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.items.ammo.AutomaticRifleCartridgeItem;
import io.github._20nickname20.imbored.game_objects.items.usable.guns.raycast.AutomaticRaycastGunItem;
import io.github._20nickname20.imbored.render.BarDisplay;
import io.github._20nickname20.imbored.util.With;

public class MinigunItem extends AutomaticRaycastGunItem {
    private final float defaultCooldown = 0.05f;
    private float overheat = 0f;

    public MinigunItem(Entity holder) {
        super(holder, 2, 0.05f, 5, 100, 100, 12, 6.5f, 0.08f, 175f, MathUtils.degRad * 10, AutomaticRifleCartridgeItem.class);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if (this.isShooting()) {
            overheat += dt / 10f;
        } else {
            overheat -= dt / 12f;
        }
        overheat = MathUtils.clamp(overheat, 0f, 1.3f);

        cooldown = defaultCooldown + defaultCooldown * overheat * 4;
        if (overheat > 0.95f) {
            cooldown = Float.MAX_VALUE;
        }
        if (overheat > 1.29f) {
            this.remove();
            if (this.getHolder() instanceof DamagableEntity damagable) {
                damagable.damage(50f);
                if (damagable instanceof CursorEntity cursorEntity) {
                    cursorEntity.b.applyLinearImpulse(cursorEntity.getCursorDirection().scl(-80), cursorEntity.b.getPosition(), true);
                }
            }
        }
    }

    private final static Color BAR_OUTTER_COLOR = new Color(0.6f, 0.2f, 0, 1);
    private final static Color BAR_INNER_COLOR = new Color(0.97f, 0.55f, 0, 1);

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        renderer.setColor(0.5f, 0.5f, 0.5f, 1);
        renderer.rectLine(-0.9f, -0.9f, 0.1f, 0.1f, 1.1f);
        renderer.rectLine(0, 0, 4f, 0, 1.55f);

        if (overheat < 0.1f) return;
        if (handHolder == null) return;

        With.rotation(renderer, -handHolder.getCursorDirection().angleDeg(), () -> {
            With.translation(renderer, 0, 6f, () -> {
                BarDisplay.render(renderer, BAR_OUTTER_COLOR, BAR_INNER_COLOR, overheat / 1.3f);
            });
        });
    }
}
