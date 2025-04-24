package io.github._20nickaname20.imbored;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;

import static io.github._20nickaname20.imbored.Main.startTime;

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

    public static CircleShape circleShape(float radius) {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        return circleShape;
    }

    public static PolygonShape boxShape(float x, float y) {
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(x, y);
        return boxShape;
    }

    public static Body createBody(World world, float x, float y, Shape shape, float density, float friction, float restitution, float mass) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        Body body = world.createBody(bodyDef);
        MassData massData = new MassData();
        massData.mass = mass;
        body.setMassData(massData);

        Fixture fixture = body.createFixture(shape, density);
        fixture.setFriction(friction);
        fixture.setRestitution(restitution);
        shape.dispose();

        return body;
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

    public static Body createStaticBox(World world, Vector2 pos, float sizeX, float sizeY) {
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(pos);

        Body body = world.createBody(boxDef);

        PolygonShape boxShape = Util.boxShape(sizeX, sizeY);
        body.createFixture(boxShape, 0.0f).setFriction(1.5f);
        boxShape.dispose();
        return body;
    }
}
