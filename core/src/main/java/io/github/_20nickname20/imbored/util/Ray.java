package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import io.github._20nickname20.imbored.render.GameRenderer;

public class Ray {
    public Ray(Vector2 p1, Vector2 p2, float spawnTime, float length, float speed, Color color) {
        this.p1 = p1;
        this.p2 = p2;
        this.direction = p2.cpy().sub(p1).nor();
        this.spawnTime = spawnTime;
        this.length = length;
        this.maxLength = p1.dst(p2);
        this.color = color;
        this.angle = direction.angleDeg();
        this.speed = speed * 4;
    }

    public Vector2 p1, p2, direction;
    public final float spawnTime, length, maxLength, angle, speed;
    public final Color color;

    public boolean render(GameRenderer renderer, float time) {
        renderer.setColor(color);
        float timeSinceSpawn = time - spawnTime;

        float pos = timeSinceSpawn * speed - length / 2;
        renderer.withTranslation(p1.x, p1.y, () -> {
            renderer.withRotation(angle, () -> {
                renderer.line(Math.min(Math.max(pos, 0f), maxLength), 0, Math.min(pos + length, maxLength), 0);
            });
        });

        return pos > maxLength + length / 2;
    }
}
