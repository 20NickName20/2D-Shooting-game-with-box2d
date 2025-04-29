package io.github._20nickname20.imbored.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PulleyJoint;
import com.badlogic.gdx.utils.Array;
import io.github._20nickname20.imbored.Entity;

import java.util.Iterator;
import java.util.function.Consumer;

public class GameRenderer extends Box2DDebugRenderer {

    public ShapeRenderer renderer;

    /** vertices for polygon rendering **/
    private final static Vector2[] vertices = new Vector2[1000];

    private final static Vector2 lower = new Vector2();
    private final static Vector2 upper = new Vector2();

    private final static Array<Body> bodies = new Array<>();
    private final static Array<Joint> joints = new Array<>();

    private boolean drawBodies;
    private boolean drawJoints;
    private boolean drawAABBs;
    private boolean drawInactiveBodies;
    private boolean drawVelocities;
    private boolean drawContacts;

    public GameRenderer(ShaderProgram shader) {
        this(true, true, false, true, false, true, shader);
    }

    public GameRenderer(boolean drawBodies, boolean drawJoints, boolean drawAABBs, boolean drawInactiveBodies,
                               boolean drawVelocities, boolean drawContacts, ShaderProgram shader) {
        // next we setup the immediate mode renderer
        renderer = new ShapeRenderer(5000, shader);
        renderer.setAutoShapeType(true);

        // initialize vertices array
        for (int i = 0; i < vertices.length; i++)
            vertices[i] = new Vector2();

        this.drawBodies = drawBodies;
        this.drawJoints = drawJoints;
        this.drawAABBs = drawAABBs;
        this.drawInactiveBodies = drawInactiveBodies;
        this.drawVelocities = drawVelocities;
        this.drawContacts = drawContacts;
    }

    public void render(World world, Matrix4 projMatrix, Consumer<ShapeRenderer> rendererConsumer) {
        renderer.setProjectionMatrix(projMatrix);
        renderer.identity();

        renderer.begin(ShapeRenderer.ShapeType.Line);
        rendererConsumer.accept(renderer);
        renderBodies(world);
        renderer.end();
    }

    public final Color SHAPE_NOT_ACTIVE = new Color(0.5f, 0.5f, 0.3f, 1);
    public final Color SHAPE_STATIC = new Color(0.5f, 0.9f, 0.5f, 1);
    public final Color SHAPE_KINEMATIC = new Color(0.5f, 0.5f, 0.9f, 1);
    public final Color SHAPE_NOT_AWAKE = new Color(0.6f, 0.6f, 0.6f, 1);
    public final Color SHAPE_AWAKE = new Color(0.9f, 0.7f, 0.7f, 1);
    public final Color JOINT_COLOR = new Color(0.5f, 0.8f, 0.8f, 1);
    public final Color AABB_COLOR = new Color(1.0f, 0, 1.0f, 1f);
    public final Color VELOCITY_COLOR = new Color(1.0f, 0, 0f, 1f);

    private void renderBodies(World world) {
        if (drawBodies || drawAABBs) {
            world.getBodies(bodies);
            for (Iterator<Body> iter = bodies.iterator(); iter.hasNext();) {
                Body body = iter.next();
                if (body.isActive() || drawInactiveBodies) renderBody(body);
            }
        }

        if (drawJoints) {
            world.getJoints(joints);
            for (Iterator<Joint> iter = joints.iterator(); iter.hasNext();) {
                Joint joint = iter.next();
                drawJoint(joint);
            }
        }
        if (drawContacts) {
            renderer.end();
            renderer.begin(ShapeRenderer.ShapeType.Point);
            for (Contact contact : world.getContactList())
                drawContact(contact);
        }
    }

    protected void renderBody(Body body) {
        Color color;
        if (body.getUserData() instanceof Entity entity) {
            color = entity.material.color;
            Vector2 position = body.getPosition();
            renderer.translate(position.x, position.y, 0);
            if (entity.render(renderer)) {
                renderer.translate(-position.x, -position.y, 0);
                return;
            }
            renderer.translate(-position.x, -position.y, 0);
        } else {
            color = getColorByBody(body);
        }
        Transform transform = body.getTransform();
        for (Fixture fixture : body.getFixtureList()) {
            if (drawBodies) {
                drawShape(fixture, transform, color);
                if (drawVelocities) {
                    Vector2 position = body.getPosition();
                    drawSegment(position, body.getLinearVelocity().add(position), VELOCITY_COLOR);
                }
            }

            if (drawAABBs) {
                drawAABB(fixture, transform);
            }
        }
    }

    private Color getColorByBody (Body body) {
        if (body.isActive() == false)
            return SHAPE_NOT_ACTIVE;
        else if (body.getType() == BodyDef.BodyType.StaticBody)
            return SHAPE_STATIC;
        else if (body.getType() == BodyDef.BodyType.KinematicBody)
            return SHAPE_KINEMATIC;
        else if (body.isAwake() == false)
            return SHAPE_NOT_AWAKE;
        else
            return SHAPE_AWAKE;
    }

    private void drawAABB (Fixture fixture, Transform transform) {
        if (fixture.getType() == Shape.Type.Circle) {

            CircleShape shape = (CircleShape)fixture.getShape();
            float radius = shape.getRadius();
            vertices[0].set(shape.getPosition());
            transform.mul(vertices[0]);
            lower.set(vertices[0].x - radius, vertices[0].y - radius);
            upper.set(vertices[0].x + radius, vertices[0].y + radius);

            // define vertices in ccw fashion...
            vertices[0].set(lower.x, lower.y);
            vertices[1].set(upper.x, lower.y);
            vertices[2].set(upper.x, upper.y);
            vertices[3].set(lower.x, upper.y);

            drawSolidPolygon(vertices, 4, AABB_COLOR, true);
        } else if (fixture.getType() == Shape.Type.Polygon) {
            PolygonShape shape = (PolygonShape)fixture.getShape();
            int vertexCount = shape.getVertexCount();

            shape.getVertex(0, vertices[0]);
            lower.set(transform.mul(vertices[0]));
            upper.set(lower);
            for (int i = 1; i < vertexCount; i++) {
                shape.getVertex(i, vertices[i]);
                transform.mul(vertices[i]);
                lower.x = Math.min(lower.x, vertices[i].x);
                lower.y = Math.min(lower.y, vertices[i].y);
                upper.x = Math.max(upper.x, vertices[i].x);
                upper.y = Math.max(upper.y, vertices[i].y);
            }

            // define vertices in ccw fashion...
            vertices[0].set(lower.x, lower.y);
            vertices[1].set(upper.x, lower.y);
            vertices[2].set(upper.x, upper.y);
            vertices[3].set(lower.x, upper.y);

            drawSolidPolygon(vertices, 4, AABB_COLOR, true);
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
            drawSolidPolygon(vertices, 2, color, true);
            return;
        }

        if (fixture.getType() == Shape.Type.Polygon) {
            PolygonShape chain = (PolygonShape)fixture.getShape();
            int vertexCount = chain.getVertexCount();
            for (int i = 0; i < vertexCount; i++) {
                chain.getVertex(i, vertices[i]);
                transform.mul(vertices[i]);
            }
            drawSolidPolygon(vertices, vertexCount, color, true);
            return;
        }

        if (fixture.getType() == Shape.Type.Chain) {
            ChainShape chain = (ChainShape)fixture.getShape();
            int vertexCount = chain.getVertexCount();
            for (int i = 0; i < vertexCount; i++) {
                chain.getVertex(i, vertices[i]);
                transform.mul(vertices[i]);
            }
            drawSolidPolygon(vertices, vertexCount, color, false);
        }
    }

    private final Vector2 f = new Vector2();
    private final Vector2 v = new Vector2();
    private final Vector2 lv = new Vector2();

    private void drawSolidCircle (Vector2 center, float radius, Vector2 axis, Color color) {
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
        renderer.line(center.x, center.y, 0, center.x + axis.x * radius, center.y + axis.y * radius, 0);
    }

    private void drawSolidPolygon (Vector2[] vertices, int vertexCount, Color color, boolean closed) {
        renderer.setColor(color.r, color.g, color.b, color.a);
        lv.set(vertices[0]);
        f.set(vertices[0]);
        for (int i = 1; i < vertexCount; i++) {
            Vector2 v = vertices[i];
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
        if (joint.getUserData() instanceof JointDisplay display) {
            if (!display.doRender) return;
            color = display.color;
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

    private void drawContact (Contact contact) {
        WorldManifold worldManifold = contact.getWorldManifold();
        if (worldManifold.getNumberOfContactPoints() == 0) return;
        Vector2 point = worldManifold.getPoints()[0];
        renderer.setColor(getColorByBody(contact.getFixtureA().getBody()));
        renderer.point(point.x, point.y, 0);
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

    public boolean isDrawAABBs () {
        return drawAABBs;
    }

    public void setDrawAABBs (boolean drawAABBs) {
        this.drawAABBs = drawAABBs;
    }

    public boolean isDrawInactiveBodies () {
        return drawInactiveBodies;
    }

    public void setDrawInactiveBodies (boolean drawInactiveBodies) {
        this.drawInactiveBodies = drawInactiveBodies;
    }

    public boolean isDrawVelocities () {
        return drawVelocities;
    }

    public void setDrawVelocities (boolean drawVelocities) {
        this.drawVelocities = drawVelocities;
    }

    public boolean isDrawContacts () {
        return drawContacts;
    }

    public void setDrawContacts (boolean drawContacts) {
        this.drawContacts = drawContacts;
    }

    public static Vector2 getAxis () {
        return axis;
    }

    public static void setAxis (Vector2 axis) {
        GameRenderer.axis = axis;
    }

    public void dispose () {
        renderer.dispose();
    }
}

