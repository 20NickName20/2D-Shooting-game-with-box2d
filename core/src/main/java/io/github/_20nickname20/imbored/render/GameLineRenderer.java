package io.github._20nickname20.imbored.render;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import io.github._20nickname20.imbored.GameWorld;
import io.github._20nickname20.imbored.game_objects.Entity;
import io.github._20nickname20.imbored.game_objects.JointEntity;
import io.github._20nickname20.imbored.util.Util;

import java.util.concurrent.atomic.AtomicBoolean;

public class GameLineRenderer implements GameRenderer {
    private final ShapeRenderer shapeRenderer;
    private final Camera camera;
    private final GameWorld gameWorld;

    public GameLineRenderer(ShaderProgram shader, GameWorld gameWorld) {
        this.shapeRenderer = new ShapeRenderer(5000, shader);
        this.gameWorld = gameWorld;
        this.camera = gameWorld.camera;
    }

    @Override
    public void line(float x1, float y1, float x2, float y2) {
        shapeRenderer.line(x1, y1, x2, y2);
    }

    private final Matrix4 savedTransform = new Matrix4();
    @Override
    public void saveTransform() {
        savedTransform.set(shapeRenderer.getTransformMatrix());
    }

    @Override
    public void loadTransform() {
        shapeRenderer.setTransformMatrix(savedTransform);
    }

    @Override
    public void resetTransform() {
        shapeRenderer.identity();
    }

    @Override
    public void setColor(float r, float g, float b, float a) {
        shapeRenderer.setColor(r, g, b, a);
    }

    @Override
    public void setColor(float r, float g, float b) {
        shapeRenderer.setColor(r, g, b, 1f);
    }

    @Override
    public void setColor(Color color) {
        shapeRenderer.setColor(color);
    }

    private final static Array<Body> bodies = new Array<>();
    private final static Array<Joint> joints = new Array<>();

    void renderBody(Body body) {
        Color color;
        Vector2 position = body.getPosition();

        if (body.getUserData() instanceof Entity entity) {
            color = entity.getMaterial().color;
            AtomicBoolean didRender = new AtomicBoolean(false);
            setColor(color);
            withTranslation(position.x, position.y, () -> {
                didRender.set(entity.render(this));
            });
            if (didRender.get()) return;
        } else {
            color = Color.PURPLE;
        }
        setColor(color);
        drawBodyDefault(body);
    }

    void drawBodyDefault(Body body) {
        Transform transform = body.getTransform();
        Vector2 position = new Vector2();
        for (Fixture fixture : body.getFixtureList()) {
            if (fixture.getType() == Shape.Type.Circle) {
                CircleShape circleShape = (CircleShape) fixture.getShape();
                position.set(circleShape.getPosition());
                transform.mul(position);
                withTranslation(position.x, position.y, () -> {
                    withRotation(transform.getRotation() * MathUtils.radDeg, () -> {
                        circle(0f, 0f, circleShape.getRadius());
                    });
                });
                return;
            }

            if (fixture.getType() == Shape.Type.Polygon) {
                PolygonShape polygon = (PolygonShape)fixture.getShape();
                int vertexCount = polygon.getVertexCount();
                float[] vertices = new float[vertexCount * 2];
                Vector2 vertex = new Vector2();
                for (int i = 0; i < vertexCount; i++) {
                    polygon.getVertex(i, vertex);
                    transform.mul(vertex);
                    vertices[i * 2] = vertex.x;
                    vertices[i * 2 + 1] = vertex.y;
                }
                polygon(vertices);
                return;
            }
        }
    }

    public void renderJoint(Joint joint) {
        Color color;
        Object userData = joint.getUserData();
        if (userData instanceof JointEntity jointEntity) {
            color = jointEntity.color;
        } else if (userData instanceof Color jointColor) {
            color = jointColor;
        } else if (userData instanceof Boolean) {
            return;
        } else {
            color = Color.CYAN;
        }
        shapeRenderer.setColor(color);
        shapeRenderer.line(joint.getAnchorA(), joint.getAnchorB());
    }

    @Override
    public void render(Runnable runnable) {
        shapeRenderer.identity();
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        runnable.run();
        gameWorld.renderRays(this);
        gameWorld.world.getBodies(bodies);
        for (Body body : bodies) {
            renderBody(body);
        }

        gameWorld.world.getJoints(joints);
        for (Joint joint : joints) {
            renderJoint(joint);
        }

        shapeRenderer.end();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void withTranslation(float x, float y, Runnable runnable) {
        Matrix4 transform = shapeRenderer.getTransformMatrix().cpy();
        shapeRenderer.translate(x, y, 0f);
        runnable.run();
        shapeRenderer.setTransformMatrix(transform);
    }

    @Override
    public void withRotation(float angle, Runnable runnable) {
        Matrix4 transform = shapeRenderer.getTransformMatrix().cpy();
        shapeRenderer.rotate(0f, 0f, 1f, angle);
        runnable.run();
        shapeRenderer.setTransformMatrix(transform);
    }

    @Override
    public void circle(float x, float y, float radius) {
        shapeRenderer.circle(x, y, radius);
    }

    @Override
    public void circle(float x, float y, float radius, int segments) {
        shapeRenderer.circle(x, y, radius, segments);
    }

    @Override
    public void drawTexture(float x, float y, TextureRegion textureRegion) {

    }

    @Override
    public void drawText(float x, float y, String text) {

    }
}
