package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

public class Raycast {
    public static class Result {
        public final float fraction;
        public final Vector2 point;
        public final Body body;

        private Result(float fraction, Vector2 point, Body body) {
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
            if (fraction < Raycast.closestFraction) {
                Raycast.closestFraction = fraction;
                Raycast.collisionPoint.set(point);
                Raycast.collisionBody = body;
            }
            return 1;
        };
    }

    public static Result castClosest(World world, Body ignored, Vector2 startPosition, Vector2 endPosition) {
        world.rayCast(Raycast.calculateCollisionPoint(ignored), startPosition, endPosition);
        if (!hadCollision) return null;
        return new Result(closestFraction, collisionPoint.cpy(), collisionBody);
    }

    public static List<Result> castAll(World world, Body ignored, Vector2 startPosition, Vector2 endPosition) {
        List<Result> results = new ArrayList<>();
        world.rayCast((fixture, point, normal, fraction) -> {
            Body body = fixture.getBody();
            if (body == ignored) return -1;
            results.add(new Result(fraction, point.cpy(), body));
            return 1;
        }, startPosition, endPosition);
        results.sort((result1, result2) -> Float.compare(result1.fraction, result2.fraction));
        return results;
    }
}
