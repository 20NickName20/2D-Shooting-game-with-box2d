package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

// TODO: make RaycastResult class
// TODO: util.ClosestRaycast.java
public class ClosestRaycast {
    public static class RaycastResult {
        public final float fraction;
        public final Vector2 point;
        public final Body body;

        private RaycastResult(float fraction, Vector2 point, Body body) {
            this.fraction = fraction;
            this.point = point;
            this.body = body;
        }
    }
    private static float closestFraction;
    private static final Vector2 collisionPoint = new Vector2();
    private static Body collisionBody;
    private static boolean hadCollision;

    public static RayCastCallback calculateCollisionPoint(Body ignored) {
        hadCollision = false;
        closestFraction = 1f;

        return (fixture, point, normal, fraction) -> {
            Body body = fixture.getBody();
            if (body == ignored) return -1;
            hadCollision = true;
            if (fraction < ClosestRaycast.closestFraction) {
                ClosestRaycast.closestFraction = fraction;
                ClosestRaycast.collisionPoint.set(point);
                ClosestRaycast.collisionBody = body;
            }
            return 1;
        };
    }

    public static RaycastResult cast(World world, Body ignored, Vector2 startPosition, Vector2 endPosition) {
        world.rayCast(ClosestRaycast.calculateCollisionPoint(ignored), startPosition, endPosition);
        if (!hadCollision) return null;
        return new RaycastResult(closestFraction, collisionPoint.cpy(), collisionBody);
    }
}
