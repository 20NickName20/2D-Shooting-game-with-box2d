package io.github._20nickname20.imbored.game_objects.items.usable;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.entities.ItemEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.UsableItem;
import io.github._20nickname20.imbored.util.ClosestRaycast;
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Util;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseGrenadeItem extends UsableItem {
    private final float power, damage, range;
    protected boolean isPinOut = false;
    protected float timeLeft;
    private boolean isExploded = false;
    private final float rayDisplayTime = 1.5f;

    public BaseGrenadeItem(Entity holder, float size, float timeLeft, float power, float damage, float range) {
        super(holder, size);
        this.timeLeft = timeLeft;
        this.power = power;
        this.damage = damage;
        this.range = range;
    }
    private final List<Ray> rays = new ArrayList<>();

    @Override
    public void update(float dt) {
        super.update(dt);
        if (isPinOut) {
            timeLeft -= dt;
        }
        if (isExploded) return;
        if (timeLeft <= 0) {
            isExploded = true;
            explode();
        }
    }

    protected final void renderRays(ShapeRenderer renderer) {
        ArrayList<Ray> toRemove = new ArrayList<>();
        float time = Util.time();
        for (Ray ray : rays) {
            float rayTime = time - ray.time;
            if (rayTime > rayDisplayTime) {
                toRemove.add(ray);
            }
            Vector2 start = new Vector2(range * ray.fraction * Math.min(1, rayTime / rayDisplayTime), 0).rotateRad(ray.offsetAngle);
            Vector2 end = new Vector2(range * ray.fraction, 0).rotateAroundRad(new Vector2(), ray.offsetAngle);
            renderer.line(start, end);
        }
        for (Ray removed : toRemove) {
            rays.remove(removed);
        }
    }

    public void shootRay(float angle) {
        Vector2 position = this.getPosition();
        Vector2 impulse = Vector2.X.cpy().rotateRad(angle);
        Vector2 endPosition = position.cpy().add(impulse.cpy().scl(range));
        ClosestRaycast.RaycastResult result = ClosestRaycast.cast(this.getHolder().world, this.getHolder().b, position, endPosition);
        if (result == null) {
            rays.add(new Ray(1, angle, Util.time()));
            return;
        }

        rays.add(new Ray(result.fraction, angle, Util.time()));
        impulse.scl(power);
        result.body.applyLinearImpulse(impulse.cpy().scl((float) (1f / Math.pow(result.body.getMass(), 0.9f))), result.point, true);
        if (result.body.getUserData() instanceof DamagableEntity entity) {
            entity.damage(damage);
        }
    }

    private void explode() {
        for (float angle = 0; angle < Math.PI * 2; angle += Math.PI / 24f) {
            shootRay(angle);
        }

        this.remove();
        if (this.getHolder() instanceof DamagableEntity damagable) {
            damagable.kill();
        }
    }

    @Override
    public void onStartUse(PlayerEntity player) {

    }

    @Override
    public void onEndUse(PlayerEntity player) {
        isPinOut = true;
    }

    @Override
    public void onDeselect(InventoryHolder holder) {

    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        renderRays(renderer);
    }
}
