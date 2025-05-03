package io.github._20nickname20.imbored.game_objects.items.usable.guns;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.util.ClosestRaycast;
import io.github._20nickname20.imbored.util.Ray;
import io.github._20nickname20.imbored.util.Util;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;
import io.github._20nickname20.imbored.game_objects.entities.InventoryHolder;
import io.github._20nickname20.imbored.game_objects.entities.living.human.CursorEntity;
import io.github._20nickname20.imbored.game_objects.entities.living.human.cursor.PlayerEntity;
import io.github._20nickname20.imbored.game_objects.items.usable.BaseGunItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RaycastGunItem extends BaseGunItem {
    private final float power;
    private final float recoilScale;
    private final float maxScatterAngle;

    private final float rayDisplayTime;
    private final List<Ray> rays = new ArrayList<>();

    // TODO: ADD LASER!!!! (shoots thru 5 entities)

    protected float range;
    public RaycastGunItem(Entity holder, float size, float cooldown, float damage, int ammo, int maxAmmo, float power, float recoilScale, float range, float maxScatterAngle, float rayDisplayTime) {
        super(holder, size, cooldown, damage, ammo, maxAmmo);
        this.power = power;
        this.recoilScale = recoilScale;
        this.range = range;
        this.maxScatterAngle = maxScatterAngle;
        this.rayDisplayTime = rayDisplayTime;
    }

    public void shootRay(PlayerEntity player, float offsetAngle) {
        Body playerBody = player.b;
        offsetAngle += MathUtils.random(-maxScatterAngle, maxScatterAngle);
        Vector2 impulse = player.cursorDirection.cpy();
        Vector2 endPosition = playerBody.getPosition().cpy().add(impulse.rotateRad(offsetAngle).cpy().scl(range));
        ClosestRaycast.RaycastResult result = ClosestRaycast.cast(player.world, playerBody, playerBody.getPosition(), endPosition);
        if (result == null) {
            rays.add(new Ray(1, offsetAngle, Util.time()));
            return;
        }

        rays.add(new Ray(result.fraction, offsetAngle, Util.time()));
        impulse.scl(power);
        result.body.applyLinearImpulse(impulse.cpy().scl((float) (1f / Math.pow(result.body.getMass(), 0.9f))), result.point, true);
        impulse.scl(-recoilScale);
        playerBody.applyLinearImpulse(impulse, playerBody.getPosition(), true);
        if (result.body.getUserData() instanceof DamagableEntity entity) {
            entity.damage(damage);
        }
    }

    @Override
    protected void onShoot(PlayerEntity player) {
        super.onShoot(player);
        shootRay(player, 0);
    }

    @Override
    public void onDeselect(InventoryHolder holder) {
        super.onDeselect(holder);
        rays.clear();
    }

    protected final void renderRays(ShapeRenderer renderer, float cursorDistance) {
        ArrayList<Ray> toRemove = new ArrayList<>();
        float time = Util.time();
        for (Ray ray : rays) {
            float rayTime = time - ray.time;
            if (rayTime > rayDisplayTime) {
                toRemove.add(ray);
            }
            Vector2 start = new Vector2(range * ray.fraction * Math.min(1, rayTime / rayDisplayTime), 0).rotateRad(ray.offsetAngle);
            Vector2 end = new Vector2(range * ray.fraction - cursorDistance, 0).rotateAroundRad(new Vector2(), ray.offsetAngle);
            renderer.line(start, end);
        }
        for (Ray removed : toRemove) {
            rays.remove(removed);
        }
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        super.render(renderer, handHolder);
        if (handHolder == null) return;
        renderRays(renderer, handHolder.getCursorDistance());
    }
}
