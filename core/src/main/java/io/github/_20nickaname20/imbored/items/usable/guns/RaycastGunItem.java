package io.github._20nickaname20.imbored.items.usable.guns;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import io.github._20nickaname20.imbored.Util;
import io.github._20nickaname20.imbored.entities.DamagableEntity;
import io.github._20nickaname20.imbored.entities.damagable.living.human.CursorEntity;
import io.github._20nickaname20.imbored.entities.damagable.living.human.cursor.PlayerEntity;
import io.github._20nickaname20.imbored.items.usable.GunItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RaycastGunItem extends GunItem {
    private float power;
    private float recoilScale;
    private float maxScatterAngle;

    private float rayDisplayTime;
    private List<Ray> rays = new ArrayList<>();

    private static class Ray {
        Ray(float fraction, float offsetAngle, float time) {
            this.fraction = fraction;
            this.offsetAngle = offsetAngle;
            this.time = time;
        }
        float fraction;
        float offsetAngle;
        float time;
    }
    // TODO: ADD LASER!!!! (shoots thru 5 entities)

    protected float range;
    public RaycastGunItem(float size, float cooldown, float damage, float power, float recoilScale, float range, float maxScatterAngle, float rayDisplayTime) {
        super(size, cooldown, damage);
        this.power = power;
        this.recoilScale = recoilScale;
        this.range = range;
        this.maxScatterAngle = maxScatterAngle;
        this.rayDisplayTime = rayDisplayTime;
    }

    public void shootRay(PlayerEntity player, float offsetAngle) {
        Body playerBody = player.b;
        offsetAngle += MathUtils.random(-maxScatterAngle, maxScatterAngle);
        Vector2 endPosition = playerBody.getPosition().cpy().add(player.cursorDirection.cpy().scl(range).rotateRad(offsetAngle));
        playerBody.getWorld().rayCast(Util.Raycast.calculateCollisionPoint(playerBody), player.b.getPosition(), endPosition);
        rays.add(new Ray(Util.Raycast.closestFraction, offsetAngle, Util.time()));
        if (!Util.Raycast.hadCollision) return;
        Util.Raycast.collisionBody.applyLinearImpulse(player.cursorDirection.cpy().scl(power), Util.Raycast.collisionPoint, true);
        playerBody.applyLinearImpulse(player.cursorDirection.cpy().scl(-power * recoilScale), playerBody.getPosition(), true);
        if (Util.Raycast.collisionBody.getUserData() instanceof DamagableEntity entity) {
            entity.damage(damage);
        }
    }

    @Override
    protected void onShoot(PlayerEntity player) {
        shootRay(player, 0);
    }

    protected void renderRays(ShapeRenderer renderer) {
        ArrayList<Ray> toRemove = new ArrayList<>();
        float time = Util.time();
        for (Ray ray : rays) {
            if (time - ray.time > rayDisplayTime) {
                toRemove.add(ray);
            }
            renderer.line(new Vector2(), new Vector2(range * ray.fraction, 0).rotateRad(ray.offsetAngle));
        }
        for (Ray removed : toRemove) {
            rays.remove(removed);
        }
    }

    @Override
    public void render(ShapeRenderer renderer, CursorEntity handHolder) {
        if (handHolder == null) return;
        renderRays(renderer);
    }
}
