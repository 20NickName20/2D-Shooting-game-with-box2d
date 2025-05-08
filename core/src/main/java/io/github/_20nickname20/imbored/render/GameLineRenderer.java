package io.github._20nickname20.imbored.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.utils.Array;
import io.github._20nickname20.imbored.game_objects.JointEntity;
import io.github._20nickname20.imbored.game_objects.Entity;

import java.util.function.Consumer;

public class GameLineRenderer extends Box2DDebugRenderer {

    public ShapeRenderer renderer;

    /** vertices for polygon rendering **/
    private final static Vector2[] vertices = new Vector2[1000];

    private final static Vector2 lower = new Vector2();
    private final static Vector2 upper = new Vector2();

    private final static Array<Body> bodies = new Array<>();
    private final static Array<Joint> joints = new Array<>();

    private boolean drawBodies;
    private boolean drawJoints;

    public GameLineRenderer(ShaderProgram shader) {
        this(true, true, shader);
    }

    public GameLineRenderer(boolean drawBodies, boolean drawJoints, ShaderProgram shader) {
        renderer = new ShapeRenderer(5000, shader);
        renderer.setAutoShapeType(true);

        // initialize vertices array
        for (int i = 0; i < vertices.length; i++)
            vertices[i] = new Vector2();

        this.drawBodies = drawBodies;
        this.drawJoints = drawJoints;
    }

    public void render(World world, Matrix4 projMatrix, Consumer<ShapeRenderer> rendererConsumer) {
        renderer.identity();
        renderer.setProjectionMatrix(projMatrix);

        renderer.begin();
        rendererConsumer.accept(renderer);
        renderBodies(world);
        renderer.end();
    }

    public final Color SHAPE_STATIC = new Color(0.5f, 0.9f, 0.5f, 1);
    public final Color JOINT_COLOR = new Color(0.5f, 0.8f, 0.8f, 1);

    private void renderBodies(World world) {
        if (drawBodies) {
            world.getBodies(bodies);
            for (Body body : bodies) {
                renderBody(body);
            }
        }

        if (drawJoints) {
            world.getJoints(joints);
            for (Joint joint : joints) {
                drawJoint(joint);
            }
        }
    }

    protected void renderBody(Body body) {
        Color color;
        Vector2 position = body.getPosition();
        if (body.getUserData() instanceof Entity entity) {
            color = entity.getMaterial().color;
            renderer.translate(position.x, position.y, 0);
            if (entity.render(renderer)) {
                renderer.translate(-position.x, -position.y, 0);
                return;
            }
            renderer.translate(-position.x, -position.y, 0);
        } else {
            color = SHAPE_STATIC;
        }
        Transform transform = body.getTransform();
        for (Fixture fixture : body.getFixtureList()) {
            if (drawBodies) {
                drawShape(fixture, transform, color);
            }
        }
    }

    private static Vector2 t = new Vector2();
    private static Vector2 axis = new Vector2();

    private void drawShape(Fixture fixture, Transform transform, Color color) {
        if (fixture.getType() == Shape.Type.Circle) {
            CircleShape circle = (CircleShape)fixture.getShape();
            t.set(circle.getPosition());
            transform.mul(t);
            drawSolidCircle(t, circle.getRadius(), axis.set(transform.vals[Transform.COS], transform.vals[Transform.SIN]), color);
            return;
        }

        if (fixture.getType() == Shape.Type.Edge) {
            EdgeShape edge = (EdgeShape)fixture.getShape();
            edge.getVertex1(vertices[0]);
            edge.getVertex2(vertices[1]);
            transform.mul(vertices[0]);
            transform.mul(vertices[1]);
            drawSolidPolygon(2, color, true);
            return;
        }

        if (fixture.getType() == Shape.Type.Polygon) {
            PolygonShape chain = (PolygonShape)fixture.getShape();
            int vertexCount = chain.getVertexCount();
            for (int i = 0; i < vertexCount; i++) {
                chain.getVertex(i, vertices[i]);
                transform.mul(vertices[i]);
            }
            drawSolidPolygon(vertexCount, color, true);
            return;
        }

        if (fixture.getType() == Shape.Type.Chain) {
            ChainShape chain = (ChainShape)fixture.getShape();
            int vertexCount = chain.getVertexCount();
            for (int i = 0; i < vertexCount; i++) {
                chain.getVertex(i, vertices[i]);
                transform.mul(vertices[i]);
            }
            drawSolidPolygon(vertexCount, color, false);
        }
    }

    private final Vector2 f = new Vector2();
    private final Vector2 v = new Vector2();
    private final Vector2 lv = new Vector2();

    private void drawSolidCircle(Vector2 center, float radius, Vector2 axis, Color color) {
        float angle = 0;
        float angleInc = 2 * (float)Math.PI / 20;
        renderer.setColor(color.r, color.g, color.b, color.a);
        for (int i = 0; i < 20; i++, angle += angleInc) {
            v.set((float)Math.cos(angle) * radius + center.x, (float)Math.sin(angle) * radius + center.y);
            if (i == 0) {
                lv.set(v);
                f.set(v);
                continue;
            }
            renderer.line(lv.x, lv.y, v.x, v.y);
            lv.set(v);
        }
        renderer.line(f.x, f.y, lv.x, lv.y);
        renderer.line(center.x, center.y, center.x + axis.x * radius, center.y + axis.y * radius);
    }

    private void drawSolidPolygon (int vertexCount, Color color, boolean closed) {
        renderer.setColor(color.r, color.g, color.b, color.a);
        lv.set(GameLineRenderer.vertices[0]);
        f.set(GameLineRenderer.vertices[0]);
        for (int i = 1; i < vertexCount; i++) {
            Vector2 v = GameLineRenderer.vertices[i];
            renderer.line(lv.x, lv.y, v.x, v.y);
            lv.set(v);
        }
        if (closed) renderer.line(f.x, f.y, lv.x, lv.y);
    }

    private void drawJoint (Joint joint) {
        Body bodyA = joint.getBodyA();
        Body bodyB = joint.getBodyB();
        Transform xf1 = bodyA.getTransform();
        Transform xf2 = bodyB.getTransform();

        Vector2 x1 = xf1.getPosition();
        Vector2 x2 = xf2.getPosition();
        Vector2 p1 = joint.getAnchorA();
        Vector2 p2 = joint.getAnchorB();

        Color color;
        if (joint.getUserData() instanceof JointEntity jointEntity) {
            color = jointEntity.color;
        } else if (joint.getUserData() instanceof Color jointColor) {
            color = jointColor;
        } else if (joint.getUserData() instanceof Boolean bool) {
            if (!bool) return;
            color = Color.BLACK;
        } else {
            color = JOINT_COLOR;
        }
        if (joint.getType() == JointDef.JointType.DistanceJoint) {
            drawSegment(p1, p2, color);
        } else if (joint.getType() == JointDef.JointType.PulleyJoint) {
            PulleyJoint pulley = (PulleyJoint)joint;
            Vector2 s1 = pulley.getGroundAnchorA();
            Vector2 s2 = pulley.getGroundAnchorB();
            drawSegment(s1, p1, color);
            drawSegment(s2, p2, color);
            drawSegment(s1, s2, color);
        } else if (joint.getType() == JointDef.JointType.MouseJoint) {
            drawSegment(joint.getAnchorA(), joint.getAnchorB(), color);
        } else {
            drawSegment(x1, p1, color);
            drawSegment(p1, p2, color);
            drawSegment(x2, p2, color);
        }
    }

    private void drawSegment (Vector2 x1, Vector2 x2, Color color) {
        renderer.setColor(color);
        renderer.line(x1.x, x1.y, x2.x, x2.y);
    }

    public boolean isDrawBodies () {
        return drawBodies;
    }

    public void setDrawBodies (boolean drawBodies) {
        this.drawBodies = drawBodies;
    }

    public boolean isDrawJoints () {
        return drawJoints;
    }
    public void setDrawJoints (boolean drawJoints) {
        this.drawJoints = drawJoints;
    }

    public void dispose () {
        renderer.dispose();
    }
}

