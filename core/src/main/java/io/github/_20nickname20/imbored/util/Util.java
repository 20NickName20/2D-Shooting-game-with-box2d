package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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

    public static float calculatePolygonArea(PolygonShape polygon) {
        int vertexCount = polygon.getVertexCount();
        Vector2[] vertices = new Vector2[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            vertices[i] = new Vector2();
            polygon.getVertex(i, vertices[i]);
        }

        float area = 0;
        for (int i = 0; i < vertexCount; i++) {
            Vector2 v1 = vertices[i];
            Vector2 v2 = vertices[(i + 1) % vertexCount]; // Wrap around to the first vertex
            area += v1.x * v2.y - v2.x * v1.y;
        }
        return Math.abs(area) / 2.0f;
    }

    public static float getArea(Shape shape) {
        if (shape instanceof PolygonShape polygonShape) {
            return calculatePolygonArea(polygonShape);
        } else {
            return (float) (shape.getRadius() * 2 * Math.PI);
        }
    }

    public static void printStackTrace(String text) {
        System.out.println(text);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < stackTrace.length; i++) {
            System.out.println((i - 1) + ".\t" + stackTrace[i].toString());
        }
    }

    public static void printStackTrace(String text, int maxLength) {
        System.out.println(text);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 2; i < Math.min(stackTrace.length, maxLength); i++) {
            System.out.println((i - 1) + ".\t" + stackTrace[i].toString());
        }
    }
}
