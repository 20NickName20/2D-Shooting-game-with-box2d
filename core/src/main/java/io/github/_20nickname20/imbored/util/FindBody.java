package io.github._20nickname20.imbored.util;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FindBody {
    public static Body atPoint(World world, Vector2 point) {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        for (Body body : bodies) {
            if (body.getType() == BodyDef.BodyType.StaticBody) continue;
            for (Fixture fixture : body.getFixtureList()) {
                if (fixture.testPoint(point)) {
                    return body;
                }
            }
        }
        return null;
    }

    public static Vector2 closestPoint(Body body, Vector2 fromPoint) {
        Vector2 closest = new Vector2();
        for (Fixture fixture : body.getFixtureList()) {
            if (fixture.testPoint(fromPoint)) {
                return fromPoint;
            }
        }
        body.getWorld().rayCast((Fixture fixture, Vector2 point, Vector2 normal, float fraction) -> {
            closest.set(point);
            return 0;
        }, fromPoint, body.getPosition());
        return closest;
    }

    public static Body closest(List<Body> bodies, Vector2 point) {
        Body closest = bodies.remove(0);
        float closestDist = closest.getPosition().dst(point);
        if (!bodies.isEmpty()) {
            for (Body body : bodies) {
                float dist = body.getPosition().dst(point);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = body;
                }
            }
        }
        return closest;
    }

    public static List<Body> filtered(World world, Function<Body, Boolean> filter) {
        List<Body> bodyList = new ArrayList<>(10);
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        for (Body body : bodies) {
            if (filter.apply(body)) {
                bodyList.add(body);
            }
        }
        return bodyList;
    }

    public static Body closestFiltered(World world, Vector2 point, Function<Body, Boolean> filter) {
        Array<Body> bodies = new Array<>();
        Body closest = null;
        float closestDist = Float.MAX_VALUE;
        world.getBodies(bodies);
        for (Body body : bodies) {
            if (filter.apply(body)) {
                float dist = body.getPosition().dst(point);
                if (closest == null) {
                    closest = body;
                    closestDist = dist;
                    continue;
                }
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = body;
                }
            }
        }
        return closest;
    }
}
