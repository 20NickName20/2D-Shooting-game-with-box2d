package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.entities.DamagableEntity;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.clamp;
import static io.github._20nickname20.imbored.Main.startTime;

public class Util {
    public static float time() {
        return (float) TimeUtils.timeSinceMillis(startTime) / 1000f;
    }

    public static void fixBleeding(TextureRegion[][] regions) {
        for (TextureRegion[] region : regions) {
            for (TextureRegion textureRegion : region) {
                fixBleeding(textureRegion);
            }
        }
    }

    public static void fixBleeding(TextureRegion region) {
        float fix = 0.01f;

        float x = region.getRegionX();
        float y = region.getRegionY();
        float width = region.getRegionWidth();
        float height = region.getRegionHeight();
        float invTexWidth = 1f / region.getTexture().getWidth();
        float invTexHeight = 1f / region.getTexture().getHeight();
        region.setRegion((x + fix) * invTexWidth, (y + fix) * invTexHeight, (x + width - fix) * invTexWidth, (y + height - fix) * invTexHeight);
    }

    public static Body createBody(World world, float x, float y, Shape shape, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);

        Fixture fixture = body.createFixture(shape, density);
        fixture.setFriction(friction);
        fixture.setRestitution(restitution);
        shape.dispose();

        return body;
    }

    private static void shootRay(GameWorld world, Body ignored, List<Ray> rays, Vector2 position, float angle, float range, float power, float damage) {
        Vector2 impulse = Vector2.X.cpy().rotateRad(angle);
        Vector2 endPosition = position.cpy().add(impulse.cpy().scl(range));
        ClosestRaycast.RaycastResult result = ClosestRaycast.cast(world.world, ignored, position, endPosition);
        if (result == null) {
            rays.add(new Ray(1, angle, Util.time()));
            return;
        }

        rays.add(new Ray(result.fraction, angle, Util.time()));
        impulse.scl(power);
        result.body.applyLinearImpulse(impulse, result.point, true);
        if (result.body.getUserData() instanceof DamagableEntity entity) {
            entity.damage(damage);
        }
    }

    public static void explode(GameWorld world, Body ignored, List<Ray> rays, Vector2 position, float stepAngle, float range, float power, float damage) {
        for (float angle = 0; angle < Math.PI * 2; angle += stepAngle) {
            shootRay(world, ignored, rays, position, angle, range, power, damage);
        }
    }

    public static Color fromHSV(final float hue, final float saturation, final float value) {
        //vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
        final float
            Kx=1f,
            Ky=2f/3f,
            Kz=1f/3f,
            Kw=3f;
        //vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
        final float
            px=Math.abs(fract(hue+Kx)*6f-Kw),
            py=Math.abs(fract(hue+Ky)*6f-Kw),
            pz=Math.abs(fract(hue+Kz)*6f-Kw);
        //return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
        return new Color(
            value*mix(Kx,clamp(px-Kx,0f,1f),saturation),
            value*mix(Kx,clamp(py-Kx,0f,1f),saturation),
            value*mix(Kx,clamp(pz-Kz,0f,1f),saturation),
            1f
        );
    }
    public static float fract(final float x) {
        return x-(int)x;
    }
    public static float mix(final float x, final float y, final float a) {
        return x*(1f-a)+y*a;
    }
}
